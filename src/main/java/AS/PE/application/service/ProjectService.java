package AS.PE.application.service;

import AS.PE.domain.exception.ResourceNotFoundException;
import AS.PE.domain.exception.UnauthorizedOperationException;
import AS.PE.domain.model.Project;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.model.enums.Status;
import AS.PE.domain.port.in.ActivateProjectUseCase;
import AS.PE.domain.port.in.CreateProjectUseCase;
import AS.PE.domain.port.in.DeleteProjectUseCase;
import AS.PE.domain.port.in.ListProjectsUseCase;
import AS.PE.domain.port.out.ProjectRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService implements CreateProjectUseCase, ListProjectsUseCase, ActivateProjectUseCase, DeleteProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Autowired
    public ProjectService(ProjectRepositoryPort projectRepositoryPort) {
        this.projectRepositoryPort = projectRepositoryPort;
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
        // Para cumplir con "el Admin puede acceder a tod0", necesitaríamos un mét2do findAll.
        // Por ahora, el admin solo ve sus propios proyectos.
        // if (userRole == Role.ADMIN) { return projectRepositoryPort.findAll(); }
        return projectRepositoryPort.findByOwnerId(authenticatedUserId);
    }

    @Override
    public Project activateProject(Long projectId, Long authenticatedUserId, Role userRole) {
        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
            throw new UnauthorizedOperationException("You do not have permission to activate this project.");
        }

        project.setStatus(Status.ACTIVE);
        return projectRepositoryPort.update(project);
    }

    @Override
    public void deleteProject(Long projectId, Long authenticatedUserId, Role userRole) {
        Project project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (!project.getOwnerId().equals(authenticatedUserId) && userRole != Role.ADMIN) {
             throw new UnauthorizedOperationException("You do not have permission to delete this project.");
        }

        project.setDeleted(true);
        projectRepositoryPort.update(project);
    }
}
