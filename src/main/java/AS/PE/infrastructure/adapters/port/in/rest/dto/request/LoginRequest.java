package AS.PE.infrastructure.adapters.port.in.rest.dto.request; // Define el paquete para los DTOs de usuario en la capa de infraestructura.

import jakarta.validation.constraints.NotBlank; // Importa la anotación para validar que el campo no esté en blanco.

public class LoginRequest { // Clase que representa la solicitud de inicio de sesión.

    @NotBlank // Asegura que el nombre de usuario no esté vacío o solo contenga espacios en blanco.
    private String username; // Campo para el nombre de usuario.

    @NotBlank // Asegura que la contraseña no esté vacía o solo contenga espacios en blanco.
    private String password; // Campo para la contraseña.

    // Getters y Setters para los campos.

    public String getUsername() { // Método para obtener el nombre de usuario.
        return username; // Retorna el nombre de usuario.
    }

    public void setUsername(String username) { // Método para establecer el nombre de usuario.
        this.username = username; // Asigna el nombre de usuario.
    }

    public String getPassword() { // Método para obtener la contraseña.
        return password; // Retorna la contraseña.
    }

    public void setPassword(String password) { // Método para establecer la contraseña.
        this.password = password; // Asigna la contraseña.
    }
}