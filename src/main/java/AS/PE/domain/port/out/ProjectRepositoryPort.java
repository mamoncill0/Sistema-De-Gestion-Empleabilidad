package AS.PE.domain.port.out;

import AS.PE.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryPort {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findByOwnerId(Long ownerId);
    Project update(Project project);
    boolean deleteById(Long id);
}
