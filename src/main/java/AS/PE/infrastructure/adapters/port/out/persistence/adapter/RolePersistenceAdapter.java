package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.model.enums.Role;
import AS.PE.domain.port.out.RoleRepositoryPort;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.RoleEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.RoleJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;

    @Autowired
    public RolePersistenceAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Role> findByName(Role name) {
        return roleJpaRepository.findByName(name)
                .map(RoleEntity::getName); // Mapea RoleEntity a Role del dominio
    }
}
