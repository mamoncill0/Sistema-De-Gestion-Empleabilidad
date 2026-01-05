package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.model.Project;
import AS.PE.domain.port.out.ProjectRepositoryPort;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.ProjectEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.ProjectJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProjectPersistenceAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository projectJpaRepository;

    @Autowired
    public ProjectPersistenceAdapter(ProjectJpaRepository projectJpaRepository) {
        this.projectJpaRepository = projectJpaRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity projectEntity = toEntity(project);
        ProjectEntity savedEntity = projectJpaRepository.save(projectEntity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Project> findByOwnerId(Long ownerId) {
        return projectJpaRepository.findByOwnerId(ownerId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Project update(Project project) {
        // El método save de JpaRepository funciona tanto para crear como para actualizar
        return save(project);
    }

    @Override
    public boolean deleteById(Long id) {
        if (projectJpaRepository.existsById(id)) {
            projectJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- Mappers ---
    private ProjectEntity toEntity(Project project) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(project.getIdProject());
        entity.setName(project.getName());
        entity.setOwnerId(project.getOwnerId());
        entity.setStatus(project.getStatus());
        entity.setDeleted(project.isDeleted());
        return entity;
    }

    private Project toDomain(ProjectEntity entity) {
        return new Project(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getStatus(),
                entity.isDeleted()
        );
    }
}
