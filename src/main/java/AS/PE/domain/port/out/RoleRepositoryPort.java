package AS.PE.domain.port.out;

import AS.PE.domain.model.enums.Role;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(Role name);
    
}
