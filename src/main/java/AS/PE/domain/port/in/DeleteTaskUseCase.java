package AS.PE.domain.port.in;

import AS.PE.domain.model.enums.Role;

public interface DeleteTaskUseCase {
    void deleteTask(Long taskId, Long authenticatedUserId, Role userRole);
}
