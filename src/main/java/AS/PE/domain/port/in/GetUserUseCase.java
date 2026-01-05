package AS.PE.domain.port.in;

import AS.PE.domain.model.User;
import java.util.Optional;

public interface GetUserUseCase {
    Optional<User> getUser(Long userId);
}
