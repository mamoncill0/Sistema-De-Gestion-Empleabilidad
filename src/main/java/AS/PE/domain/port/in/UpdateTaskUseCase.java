package AS.PE.domain.port.in;

import AS.PE.domain.model.Task;

public interface UpdateTaskUseCase {
    Task updateTask(Task task);
}
