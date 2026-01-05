package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.model.Project;
import AS.PE.domain.port.out.ProjectRepositoryPort;
import AS.PE.infrastructure.adapters.port.out.persistence.mapper.ProjectMapper;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.ProjectJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProjectPersistenceAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectPersistenceAdapter(ProjectJpaRepository projectJpaRepository, ProjectMapper projectMapper) {
        this.projectJpaRepository = projectJpaRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public Project save(Project project) {
        return projectMapper.toDomain(
                projectJpaRepository.save(projectMapper.toEntity(project))
        );
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectJpaRepository.findById(id)
                .map(projectMapper::toDomain);
    }

    @Override
    public List<Project> findByOwnerId(Long ownerId) {
        return projectJpaRepository.findByOwnerId(ownerId).stream()
                .map(projectMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Project update(Project project) {
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
}