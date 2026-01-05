package AS.PE.infrastructure.adapters.port.in.rest;

import AS.PE.domain.model.Project;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.port.in.ActivateProjectUseCase;
import AS.PE.domain.port.in.CreateProjectUseCase;
import AS.PE.domain.port.in.DeleteProjectUseCase;
import AS.PE.domain.port.in.ListProjectsUseCase;
import AS.PE.infrastructure.adapters.port.in.rest.dto.request.CreateProjectRequest;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ListProjectsUseCase listProjectsUseCase;
    private final ActivateProjectUseCase activateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;

    @Autowired
    public ProjectController(CreateProjectUseCase createProjectUseCase, ListProjectsUseCase listProjectsUseCase, ActivateProjectUseCase activateProjectUseCase, DeleteProjectUseCase deleteProjectUseCase) {
        this.createProjectUseCase = createProjectUseCase;
        this.listProjectsUseCase = listProjectsUseCase;
        this.activateProjectUseCase = activateProjectUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Project> createProject(@Valid @RequestBody CreateProjectRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        Long ownerId = userDetails.getId();
        Role userRole = getRoleFromAuthentication(authentication);

        Project newProject = createProjectUseCase.createProject(request.getName(), ownerId, ownerId, userRole);
        return ResponseEntity.ok(newProject);
    }

    @GetMapping
    @Operation(summary = "List projects")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Project>> getProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();
        Role userRole = getRoleFromAuthentication(authentication);

        List<Project> projects = listProjectsUseCase.listProjects(authenticatedUserId, userRole);
        return ResponseEntity.ok(projects);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a project")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Project> activateProject(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();
        Role userRole = getRoleFromAuthentication(authentication);

        Project activatedProject = activateProjectUseCase.activateProject(id, authenticatedUserId, userRole);
        return ResponseEntity.ok(activatedProject);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project (USER can delete their own, ADMIN can delete any)")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProject(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();
        Role userRole = getRoleFromAuthentication(authentication);

        deleteProjectUseCase.deleteProject(id, authenticatedUserId, userRole);
        return ResponseEntity.ok(Map.of("message", "Proyecto eliminado exitosamente"));
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
