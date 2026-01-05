package AS.PE.application.service;

import AS.PE.domain.model.User;
import AS.PE.domain.port.in.DeleteUserUseCase;
import AS.PE.domain.port.in.GetUserUseCase;
import AS.PE.domain.port.in.ListUsersUseCase;
import AS.PE.domain.port.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements GetUserUseCase, DeleteUserUseCase, ListUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Autowired
    public UserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Optional<User> getUser(Long userId) {
        return userRepositoryPort.findById(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        // Validar si el usuario existe antes de borrar
        if (userRepositoryPort.findById(userId).isEmpty()) {
            // Opcional: lanzar una excepción si el usuario no se encuentra
            // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            return; // O simplemente no hacer nada si no existe
        }
        userRepositoryPort.deleteById(userId);
    }

    @Override
    public List<User> listUsers() {
        return userRepositoryPort.findAll();
    }
}
