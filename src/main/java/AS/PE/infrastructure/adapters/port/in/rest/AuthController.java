package AS.PE.infrastructure.adapters.port.in.rest;

import AS.PE.application.service.AuthService;
import AS.PE.infrastructure.adapters.port.in.rest.dto.request.LoginRequest;
import AS.PE.infrastructure.adapters.port.in.rest.dto.request.RegisterRequest;
import AS.PE.infrastructure.adapters.port.in.rest.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController // Marca esta clase como un controlador REST.
@RequestMapping("/auth") // Mapea todas las peticiones que comiencen con /auth a este controlador.
public class AuthController { // Clase controladora para la autenticación de usuarios.

    private final AuthService authService; // Inyecta el AuthService de la capa de aplicación.

    @Autowired // Constructor para inyección de dependencias.
    public AuthController(AuthService authService) {
        this.authService = authService; // Asigna el AuthService inyectado.
    }

    @PostMapping("/login") // Mapea las peticiones POST a /auth/login.
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) { // Método para autenticar usuarios.
        // Delega la lógica de autenticación al AuthService.
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(authResponse); // Retorna la respuesta de autenticación.
    }

    @PostMapping("/register") // Mapea las peticiones POST a /auth/register.
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) { // Método para registrar nuevos usuarios.
        try {
            // Delega la lógica de registro al AuthService.
            authService.registerUser(signUpRequest);
            return ResponseEntity.ok("User registered successfully!"); // Retorna un mensaje de éxito.
        } catch (ResponseStatusException ex) {
            // Captura excepciones lanzadas por el AuthService y retorna la respuesta HTTP adecuada.
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }
}