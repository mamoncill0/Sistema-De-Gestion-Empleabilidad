package AS.PE.infrastructure.adapters.port.out.persistence.mapper;

import AS.PE.domain.model.Task;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskEntity toEntity(Task task) {
        if (task == null) return null;

        TaskEntity entity = new TaskEntity();
        entity.setId(task.getIdTask());
        entity.setProjectId(task.getProjectId());
        entity.setTitle(task.getTitle());
        entity.setCompleted(task.isCompleted());
        entity.setDeleted(task.isDeleted());
        return entity;
    }

    public Task toDomain(TaskEntity entity) {
        if (entity == null) return null;

        return new Task(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.isCompleted(),
                entity.isDeleted()
        );
    }
}