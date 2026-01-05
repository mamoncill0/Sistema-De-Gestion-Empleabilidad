package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.model.Task;
import AS.PE.domain.port.out.TaskRepositoryPort;
import AS.PE.infrastructure.adapters.port.out.persistence.mapper.TaskMapper;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.TaskJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository, TaskMapper taskMapper) {
        this.taskJpaRepository = taskJpaRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public Task save(Task task) {
        return taskMapper.toDomain(
                taskJpaRepository.save(taskMapper.toEntity(task))
        );
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskJpaRepository.findById(id)
                .map(taskMapper::toDomain);
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return taskJpaRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toDomain)
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
}