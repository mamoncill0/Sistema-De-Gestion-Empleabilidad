package AS.PE.domain.port.in;

import AS.PE.domain.model.User;

public interface CreateUserUseCase {
    User createUser(User user);
}
