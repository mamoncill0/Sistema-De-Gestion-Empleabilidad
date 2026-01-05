package AS.PE.application.service;

import AS.PE.domain.exception.ResourceNotFoundException;
import AS.PE.domain.exception.TaskCompletionException;
import AS.PE.domain.exception.UnauthorizedOperationException;
import AS.PE.domain.model.Project;
import AS.PE.domain.model.Task;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.port.in.CompleteTaskUseCase;
import AS.PE.domain.port.in.CreateTaskUseCase;
import AS.PE.domain.port.in.DeleteTaskUseCase;
import AS.PE.domain.port.out.AuditLogPort;
import AS.PE.domain.port.out.NotificationPort;
import AS.PE.domain.port.out.ProjectRepositoryPort;
import AS.PE.domain.port.out.TaskRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService implements CreateTaskUseCase, CompleteTaskUseCase, DeleteTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    @Autowired
    public TaskService(TaskRepositoryPort taskRepositoryPort,
                       ProjectRepositoryPort projectRepositoryPort,
                       AuditLogPort auditLogPort,
                       NotificationPort notificationPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.projectRepositoryPort = projectRepositoryPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public Task createTask(Long projectId, String title, Long authenticatedUserId, Role userRole) {
        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // REGLA: Solo el propietario puede modificar sus tareas
        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to add tasks to this project.");
        }

        Task task = new Task();
        task.setProjectId(projectId);
        task.setTitle(title);
        task.setCompleted(false);
        task.setDeleted(false);

        return taskRepositoryPort.save(task);
    }

    @Override
    public Task completeTask(Long taskId, Long authenticatedUserId, Role userRole) {
        // 1. Validar que la tarea existe
        Task task = taskRepositoryPort.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // 2. REGLA: Una tarea completada no puede modificarse
        if (task.isCompleted()) {
            throw new TaskCompletionException("Cannot complete task: task is already completed.");
        }

        // 3. Validar permisos
        Project project = projectRepositoryPort.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project associated with task not found"));

        // REGLA: Solo el propietario puede modificar
        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to modify this task.");
        }

        // 4. Completar la tarea
        task.setCompleted(true);
        Task updatedTask = taskRepositoryPort.update(task);

        // 5. REGLA: La finalización de tareas debe generar auditoría
        auditLogPort.register("TASK_COMPLETED", taskId, "Task");

        // 6. REGLA: La finalización de tareas debe generar notificación
        notificationPort.notify(String.format("Task '%s' (ID: %d) has been completed.",
                task.getTitle(), taskId));

        return updatedTask;
    }

    @Override
    public void deleteTask(Long taskId, Long authenticatedUserId, Role userRole) {
        Task task = taskRepositoryPort.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        Project project = projectRepositoryPort.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project associated with task not found"));

        // REGLA: Solo el propietario puede modificar
        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to delete this task.");
        }

        // REGLA: Todas las eliminaciones son lógicas (soft delete)
        task.setDeleted(true);
        taskRepositoryPort.update(task);
    }
}