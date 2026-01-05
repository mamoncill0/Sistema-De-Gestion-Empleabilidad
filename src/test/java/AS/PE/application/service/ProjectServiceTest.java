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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

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
    private Task activeTask;
    private Task completedTask;
    private Long projectId = 1L;
    private Long ownerId = 100L;
    private Long otherUserId = 200L;

    @BeforeEach
    void setUp() {
        testProject = new Project(projectId, ownerId, "Test Project", Status.DRAFT, false);

        activeTask = new Task(1L, projectId, "Active Task", false, false);
        completedTask = new Task(2L, projectId, "Completed Task", true, false);
    }

    @Test
    @DisplayName("ActivateProject_WithTasks_ShouldSucceed")
    void activateProject_WithActiveTasks_ShouldSucceed() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepositoryPort.findByProjectId(projectId)).thenReturn(Arrays.asList(activeTask));
        when(projectRepositoryPort.update(any(Project.class))).thenReturn(testProject);

        // Act
        Project result = projectService.activateProject(projectId, ownerId, Role.USER);

        // Assert
        assertNotNull(result);
        assertEquals(Status.ACTIVE, testProject.getStatus());
        verify(auditLogPort, times(1)).register(eq("PROJECT_ACTIVATED"), eq(projectId), eq("Project"));
        verify(notificationPort, times(1)).notify(contains("has been activated"));
        verify(projectRepositoryPort, times(1)).update(testProject);
    }

    @Test
    @DisplayName("ActivateProject_WithoutTasks_ShouldFail")
    void activateProject_WithoutActiveTasks_ShouldFail() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepositoryPort.findByProjectId(projectId)).thenReturn(Collections.emptyList());

        // Act & Assert
        ProjectActivationException exception = assertThrows(
                ProjectActivationException.class,
                () -> projectService.activateProject(projectId, ownerId, Role.USER)
        );

        assertTrue(exception.getMessage().contains("no active tasks found"));
        verify(auditLogPort, never()).register(anyString(), anyLong(), anyString());
        verify(notificationPort, never()).notify(anyString());
        verify(projectRepositoryPort, never()).update(any(Project.class));
    }

    @Test
    @DisplayName("ActivateProject_ByNonOwner_ShouldFail")
    void activateProject_ByNonOwner_ShouldFail() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));

        // Act & Assert
        UnauthorizedOperationException exception = assertThrows(
                UnauthorizedOperationException.class,
                () -> projectService.activateProject(projectId, otherUserId, Role.USER)
        );

        assertTrue(exception.getMessage().contains("do not have permission"));
        verify(taskRepositoryPort, never()).findByProjectId(anyLong());
        verify(auditLogPort, never()).register(anyString(), anyLong(), anyString());
        verify(notificationPort, never()).notify(anyString());
    }

    @Test
    @DisplayName("ActivateProject_WithOnlyCompletedTasks_ShouldFail")
    void activateProject_WithOnlyCompletedTasks_ShouldFail() {
        // Arrange
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepositoryPort.findByProjectId(projectId)).thenReturn(Arrays.asList(completedTask));

        // Act & Assert
        ProjectActivationException exception = assertThrows(
                ProjectActivationException.class,
                () -> projectService.activateProject(projectId, ownerId, Role.USER)
        );

        assertTrue(exception.getMessage().contains("no active tasks found"));
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
                () -> projectService.activateProject(projectId, ownerId, Role.USER)
        );

        verify(taskRepositoryPort, never()).findByProjectId(anyLong());
        verify(auditLogPort, never()).register(anyString(), anyLong(), anyString());
    }
}