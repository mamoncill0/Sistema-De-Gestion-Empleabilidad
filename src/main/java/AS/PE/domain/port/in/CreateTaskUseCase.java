package AS.PE.domain.port.in;

import AS.PE.domain.model.Task;
import AS.PE.domain.model.enums.Role;

public interface CreateTaskUseCase {
    Task createTask(Long projectId, String title, Long authenticatedUserId, Role userRole);
}
