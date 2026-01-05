package AS.PE.domain.port.out;

import AS.PE.domain.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findByProjectId(Long projectId);
    Task update(Task task);
    boolean deleteById(Long id);
}
