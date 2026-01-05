package AS.PE.application.service;

import AS.PE.domain.exception.ProjectActivationException;
import AS.PE.domain.exception.ResourceNotFoundException;
import AS.PE.domain.exception.UnauthorizedOperationException;
import AS.PE.domain.model.Project;
import AS.PE.domain.model.Task;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.model.enums.Status;
import AS.PE.domain.port.in.ActivateProjectUseCase;
import AS.PE.domain.port.in.CreateProjectUseCase;
import AS.PE.domain.port.in.DeleteProjectUseCase;
import AS.PE.domain.port.in.ListProjectsUseCase;
import AS.PE.domain.port.out.AuditLogPort;
import AS.PE.domain.port.out.NotificationPort;
import AS.PE.domain.port.out.ProjectRepositoryPort;
import AS.PE.domain.port.out.TaskRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService implements CreateProjectUseCase, ListProjectsUseCase, ActivateProjectUseCase, DeleteProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;
    private final TaskRepositoryPort taskRepositoryPort;
    private final AuditLogPort auditLogPort;
    private final NotificationPort notificationPort;

    @Autowired
    public ProjectService(ProjectRepositoryPort projectRepositoryPort,
                          TaskRepositoryPort taskRepositoryPort,
                          AuditLogPort auditLogPort,
                          NotificationPort notificationPort) {
        this.projectRepositoryPort = projectRepositoryPort;
        this.taskRepositoryPort = taskRepositoryPort;
        this.auditLogPort = auditLogPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public Project createProject(String name, Long ownerId, Long authenticatedUserId, Role userRole) {
        if (!ownerId.equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You can only create projects for yourself.");
        }

        Project project = new Project();
        project.setName(name);
        project.setOwnerId(ownerId);
        project.setStatus(Status.DRAFT);
        project.setDeleted(false);

        return projectRepositoryPort.save(project);
    }

    @Override
    public List<Project> listProjects(Long authenticatedUserId, Role userRole) {
        return projectRepositoryPort.findByOwnerId(authenticatedUserId);
    }

    @Override
    public Project activateProject(Long projectId, Long authenticatedUserId, Role userRole) {
        // 1. Validar que el proyecto existe
        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // 2. REGLA: Solo el propietario puede modificar un proyecto (o ADMIN)
        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to activate this project.");
        }

        // 3. REGLA: Un proyecto solo puede activarse si tiene al menos una tarea activa
        List<Task> tasks = taskRepositoryPort.findByProjectId(projectId);
        boolean hasActiveTasks = tasks.stream()
                .anyMatch(task -> !task.isDeleted() && !task.isCompleted());

        if (!hasActiveTasks) {
            throw new ProjectActivationException("Cannot activate project: no active tasks found. Add at least one task first.");
        }

        // 4. Activar el proyecto
        project.setStatus(Status.ACTIVE);
        Project updatedProject = projectRepositoryPort.update(project);

        // 5. REGLA: La activación de proyectos debe generar auditoría
        auditLogPort.register("PROJECT_ACTIVATED", projectId, "Project");

        // 6. REGLA: La activación de proyectos debe generar notificación
        notificationPort.notify(String.format("Project '%s' (ID: %d) has been activated.",
                project.getName(), projectId));

        return updatedProject;
    }

    @Override
    public void deleteProject(Long projectId, Long authenticatedUserId, Role userRole) {
        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // REGLA: Solo el propietario puede modificar un proyecto
        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to delete this project.");
        }

        // REGLA: Todas las eliminaciones son lógicas (soft delete)
        project.setDeleted(true);
        projectRepositoryPort.update(project);
    }
}