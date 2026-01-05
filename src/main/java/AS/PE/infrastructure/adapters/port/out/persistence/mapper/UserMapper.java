package AS.PE.infrastructure.adapters.port.out.persistence.mapper;

import AS.PE.domain.model.User;
import AS.PE.domain.model.enums.Role;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        Role role = null;
        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            role = entity.getRoles().iterator().next().getName();
        }

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                role
        );
    }
}