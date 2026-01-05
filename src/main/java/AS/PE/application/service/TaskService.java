package AS.PE.application.service;

import AS.PE.domain.exception.ResourceNotFoundException;
import AS.PE.domain.exception.UnauthorizedOperationException;
import AS.PE.domain.model.Project;
import AS.PE.domain.model.Task;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.port.in.CompleteTaskUseCase;
import AS.PE.domain.port.in.CreateTaskUseCase;
import AS.PE.domain.port.in.DeleteTaskUseCase;
import AS.PE.domain.port.out.ProjectRepositoryPort;
import AS.PE.domain.port.out.TaskRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService implements CreateTaskUseCase, CompleteTaskUseCase, DeleteTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;

    @Autowired
    public TaskService(TaskRepositoryPort taskRepositoryPort, ProjectRepositoryPort projectRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.projectRepositoryPort = projectRepositoryPort;
    }

    @Override
    public Task createTask(Long projectId, String title, Long authenticatedUserId, Role userRole) {
        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

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
        Task task = taskRepositoryPort.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        Project project = projectRepositoryPort.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project associated with task not found"));

        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to modify this task.");
        }

        task.setCompleted(true);
        return taskRepositoryPort.update(task);
    }

    @Override
    public void deleteTask(Long taskId, Long authenticatedUserId, Role userRole) {
        Task task = taskRepositoryPort.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        Project project = projectRepositoryPort.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project associated with task not found"));

        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to delete this task.");
        }

        task.setDeleted(true);
        taskRepositoryPort.update(task);
    }
}
