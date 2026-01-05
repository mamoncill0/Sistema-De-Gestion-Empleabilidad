package AS.PE.domain.port.in;

import AS.PE.domain.model.Project;
import AS.PE.domain.model.enums.Role;

import java.util.List;

public interface ListProjectsUseCase {
    List<Project> listProjects(Long authenticatedUserId, Role userRole);
}
