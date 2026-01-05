package AS.PE.infrastructure.adapters.port.out.persistence.mapper;

import AS.PE.domain.model.Project;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectEntity toEntity(Project project) {
        if (project == null) return null;

        ProjectEntity entity = new ProjectEntity();
        entity.setId(project.getIdProject());
        entity.setName(project.getName());
        entity.setOwnerId(project.getOwnerId());
        entity.setStatus(project.getStatus());
        entity.setDeleted(project.isDeleted());
        return entity;
    }

    public Project toDomain(ProjectEntity entity) {
        if (entity == null) return null;

        return new Project(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getStatus(),
                entity.isDeleted()
        );
    }
}