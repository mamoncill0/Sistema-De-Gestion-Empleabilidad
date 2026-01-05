package AS.PE.infrastructure.adapters.port.in.rest;

import AS.PE.domain.model.Task;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.port.in.CompleteTaskUseCase;
import AS.PE.domain.port.in.CreateTaskUseCase;
import AS.PE.domain.port.in.DeleteTaskUseCase;
import AS.PE.infrastructure.adapters.port.in.rest.dto.request.CreateTaskRequest;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Endpoints for managing tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final CompleteTaskUseCase completeTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    @Autowired
    public TaskController(CreateTaskUseCase createTaskUseCase, CompleteTaskUseCase completeTaskUseCase, DeleteTaskUseCase deleteTaskUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.completeTaskUseCase = completeTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @PostMapping("/projects/{projectId}")
    @Operation(summary = "Create a new task within a project")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId, @Valid @RequestBody CreateTaskRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();
        Role userRole = getRoleFromAuthentication(authentication);

        Task newTask = createTaskUseCase.createTask(projectId, request.getTitle(), authenticatedUserId, userRole);
        return ResponseEntity.ok(newTask);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a task as complete")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();
        Role userRole = getRoleFromAuthentication(authentication);

        Task completedTask = completeTaskUseCase.completeTask(id, authenticatedUserId, userRole);
        return ResponseEntity.ok(completedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task (USER can delete their own, ADMIN can delete any)")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();
        Role userRole = getRoleFromAuthentication(authentication);

        deleteTaskUseCase.deleteTask(id, authenticatedUserId, userRole);
        return ResponseEntity.ok(Map.of("message", "Tarea eliminada exitosamente"));
    }

    private Role getRoleFromAuthentication(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(s -> s.replace("ROLE_", ""))
                .map(Role::valueOf)
                .findFirst()
                .orElse(null);
    }
}
