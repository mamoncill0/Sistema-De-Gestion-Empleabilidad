package AS.PE.infrastructure.adapters.port.out.persistence.mapper;

import AS.PE.domain.exception.RoleNotFoundException;
import AS.PE.domain.model.User;
import AS.PE.domain.model.enums.Role;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.RoleEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.UserEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.RoleJpaRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private RoleJpaRepository roleJpaRepository;

    @Mapping(source = "idUser", target = "id")
    @Mapping(source = "role", target = "roles")
    public abstract UserEntity toEntity(User user);

    @Mapping(source = "id", target = "idUser")
    @Mapping(source = "roles", target = "role")
    public abstract User toDomain(UserEntity userEntity);

    protected Set<RoleEntity> mapRoleToRoleEntity(Role role) {
        if (role == null) {
            return Collections.emptySet();
        }
        // Find the managed RoleEntity from the database
        RoleEntity roleEntity = roleJpaRepository.findByName(role)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + role.name()));
        return Collections.singleton(roleEntity);
    }

    protected Role mapRoleEntityToRole(Set<RoleEntity> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        // A user has one role, so we get the first from the set
        return roles.iterator().next().getName();
    }
}
