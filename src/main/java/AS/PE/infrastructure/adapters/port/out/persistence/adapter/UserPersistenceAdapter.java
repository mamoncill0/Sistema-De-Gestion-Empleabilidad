package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.model.User;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.port.out.UserRepositoryPort;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.RoleEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.UserEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.mapper.UserMapper;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.RoleJpaRepository;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper mapper;

    @Autowired
    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, RoleJpaRepository roleJpaRepository, UserMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(this.mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this.mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    // Métodos de mapeo manual (Mapper)
    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getIdUser());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        
        if (user.getRole() != null) {
            // Buscamos el rol en la BD para asegurar que sea una entidad gestionada
            Optional<RoleEntity> roleEntityOpt = roleJpaRepository.findByName(user.getRole());
            if (roleEntityOpt.isPresent()) {
                entity.setRoles(Set.of(roleEntityOpt.get()));
            } else {
                // Si no existe (caso raro si se validó antes), creamos uno temporal o lanzamos error
                // Para este adaptador, asumiremos que se crea una nueva instancia si es necesario
                // aunque lo ideal es que los roles ya existan en BD.
                entity.setRoles(Set.of(new RoleEntity(user.getRole())));
            }
        }
        return entity;
    }
}
