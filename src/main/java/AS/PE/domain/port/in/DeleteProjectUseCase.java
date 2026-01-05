package AS.PE.domain.port.in;

import AS.PE.domain.model.enums.Role;

public interface DeleteProjectUseCase {
    void deleteProject(Long projectId, Long authenticatedUserId, Role userRole);
}
