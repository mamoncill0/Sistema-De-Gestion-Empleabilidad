package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.model.Task;
import AS.PE.domain.port.out.TaskRepositoryPort;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.TaskEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.TaskJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository taskJpaRepository;

    @Autowired
    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository) {
        this.taskJpaRepository = taskJpaRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity taskEntity = toEntity(task);
        TaskEntity savedEntity = taskJpaRepository.save(taskEntity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return taskJpaRepository.findByProjectId(projectId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Task update(Task task) {
        return save(task);
    }

    @Override
    public boolean deleteById(Long id) {
        if (taskJpaRepository.existsById(id)) {
            taskJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- Mappers ---
    private TaskEntity toEntity(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.setId(task.getIdTask());
        entity.setProjectId(task.getProjectId());
        entity.setTitle(task.getTitle());
        entity.setCompleted(task.isCompleted());
        entity.setDeleted(task.isDeleted());
        return entity;
    }

    private Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.isCompleted(),
                entity.isDeleted()
        );
    }
}
