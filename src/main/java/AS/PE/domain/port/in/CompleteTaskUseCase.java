package AS.PE.domain.port.in;

import AS.PE.domain.model.Task;
import AS.PE.domain.model.enums.Role;

public interface CompleteTaskUseCase {
    Task completeTask(Long taskId, Long authenticatedUserId, Role userRole);
}
