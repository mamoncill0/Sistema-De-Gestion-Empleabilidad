package AS.PE.application.service;

import AS.PE.domain.exception.ResourceNotFoundException;
import AS.PE.domain.exception.TaskCompletionException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteTaskUseCaseTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @Mock
    private AuditLogPort auditLogPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private TaskService taskService;

    private Project testProject;
    private Task testTask;
    private final Long taskId = 1L;
    private final Long projectId = 10L;
    private final Long ownerId = 100L;
    private final Long otherUserId = 200L;

    @BeforeEach
    void setUp() {
        testProject = new Project(projectId, ownerId, "Test Project", Status.ACTIVE, false);
        testTask = new Task(taskId, projectId, "Test Task", false, false);
    }

    @Test
    @DisplayName("CompleteTask_AlreadyCompleted_ShouldFail")
    void completeTask_AlreadyCompleted_ShouldFail() {
        // Arrange
        testTask.setCompleted(true);
        when(taskRepositoryPort.findById(taskId)).thenReturn(Optional.of(testTask));

        // Act & Assert
        TaskCompletionException exception = assertThrows(
                TaskCompletionException.class,
                () -> taskService.completeTask(taskId, ownerId, Role.ADMIN)
        );
        assertEquals("Cannot complete task: task is already completed.", exception.getMessage());
        verify(taskRepositoryPort, never()).update(any(Task.class));
    }

    @Test
    @DisplayName("CompleteTask_ShouldGenerateAuditAndNotification")
    void completeTask_ShouldGenerateAuditAndNotification() {
        // Arrange
        when(taskRepositoryPort.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepositoryPort.update(any(Task.class))).thenReturn(testTask);

        // Act
        taskService.completeTask(taskId, ownerId, Role.ADMIN);

        // Assert
        assertTrue(testTask.isCompleted());
        verify(taskRepositoryPort).update(testTask);
        verify(auditLogPort).register(eq("TASK_COMPLETED"), eq(taskId), anyString());
        verify(notificationPort).notify(anyString());
    }

    @Test
    @DisplayName("CompleteTask_ByNonOwner_ShouldFail")
    void completeTask_ByNonOwner_ShouldFail() {
        // Arrange
        when(taskRepositoryPort.findById(taskId)).thenReturn(Optional.of(testTask));
        when(projectRepositoryPort.findById(projectId)).thenReturn(Optional.of(testProject));

        // Act & Assert
        assertThrows(
                UnauthorizedOperationException.class,
                () -> taskService.completeTask(taskId, otherUserId, Role.USER)
        );
    }

    @Test
    @DisplayName("CompleteTask_TaskNotFound_ShouldFail")
    void completeTask_TaskNotFound_ShouldFail() {
        // Arrange
        when(taskRepositoryPort.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.completeTask(taskId, ownerId, Role.ADMIN)
        );
    }
}
