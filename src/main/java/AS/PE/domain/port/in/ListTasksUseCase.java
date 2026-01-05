package AS.PE.domain.port.in;

import AS.PE.domain.model.Task;
import java.util.List;

public interface ListTasksUseCase {
    List<Task> listTasksByProject(Long projectId);
}
