package AS.PE.domain.port.in;

import AS.PE.domain.model.User;
import java.util.List;

public interface ListUsersUseCase {
    List<User> listUsers();
}
