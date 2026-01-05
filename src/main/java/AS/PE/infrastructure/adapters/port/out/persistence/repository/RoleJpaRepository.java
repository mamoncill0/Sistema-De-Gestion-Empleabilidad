package AS.PE.infrastructure.adapters.port.out.persistence.repository;

import AS.PE.domain.model.enums.Role;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(Role name);
}
