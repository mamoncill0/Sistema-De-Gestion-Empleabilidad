package AS.PE.domain.port.in;

import AS.PE.domain.model.Project;
import AS.PE.domain.model.enums.Role;

public interface ActivateProjectUseCase {
    Project activateProject(Long projectId, Long authenticatedUserId, Role userRole);
}
