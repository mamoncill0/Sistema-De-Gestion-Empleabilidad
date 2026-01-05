package AS.PE.infrastructure.adapters.port.out.persistence.repository;

import AS.PE.infrastructure.adapters.port.out.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findByOwnerId(Long ownerId);
}
