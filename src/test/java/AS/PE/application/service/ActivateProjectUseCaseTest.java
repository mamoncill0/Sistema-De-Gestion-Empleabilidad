package AS.PE.application.service;

import AS.PE.domain.exception.ProjectActivationException;
import AS.PE.domain.exception.ResourceNotFoundException;
import AS.PE.domain.exception.UnauthorizedOperationException;
import AS.PE.domain.model.Project;
import AS.PE.domain.model.Task;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.model.enums.Status;
import AS.PE.domain.port.out.AuditLogPort;
import AS.PE.domain.port.out.NotificationPort;
import AS.PE.domain.port.out.ProjectRepositoryPort;
import AS.PE.domain.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateProjectUseCaseTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @Mock
    private TaskRepositoryPort taskRepositoryPort;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private final Long projectId = 1L;
    private final Long ownerId = 100L;
    private final Long nonOwnerId = 200L;

    @BeforeEach
    void setUp() {
        testProject = new Project(projectId, ownerId, "Test Project", Status.DRAFT, false);
    }

    @Test
    @DisplayName("ActivateProject_WithTasks_ShouldSucceed")
    void activateProject_WithTasks_ShouldSucceed() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepositoryPort.findByProjectId(projectId)).thenReturn(List.of(new Task()));
        // Fix: Mock the 'update' method instead of 'save'
        when(projectRepositoryPort.update(any(Project.class))).thenReturn(testProject);

        // Act
        Project activatedProject = projectService.activateProject(projectId, ownerId, Role.ADMIN);

        // Assert
        assertNotNull(activatedProject);
        assertEquals(Status.ACTIVE, activatedProject.getStatus());
        verify(projectRepositoryPort).update(testProject);
    }

    @Test
    @DisplayName("ActivateProject_WithoutTasks_ShouldFail")
    void activateProject_WithoutTasks_ShouldFail() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepositoryPort.findByProjectId(projectId)).thenReturn(Collections.emptyList());

        // Act & Assert
        ProjectActivationException exception = assertThrows(
                ProjectActivationException.class,
                () -> projectService.activateProject(projectId, ownerId, Role.ADMIN)
        );
        // Fix: Update the expected exception message
        assertEquals("Cannot activate project: no active tasks found. Add at least one task first.", exception.getMessage());
        verify(projectRepositoryPort, never()).update(any(Project.class));
    }

    @Test
    @DisplayName("ActivateProject_ByNonOwner_ShouldFail")
    void activateProject_ByNonOwner_ShouldFail() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));

        // Act & Assert
        assertThrows(
                UnauthorizedOperationException.class,
                () -> projectService.activateProject(projectId, nonOwnerId, Role.USER)
        );
        verify(projectRepositoryPort, never()).update(any(Project.class));
    }

    @Test
    @DisplayName("ActivateProject_ProjectNotFound_ShouldFail")
    void activateProject_ProjectNotFound_ShouldFail() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.activateProject(projectId, ownerId, Role.ADMIN)
        );
    }
}
