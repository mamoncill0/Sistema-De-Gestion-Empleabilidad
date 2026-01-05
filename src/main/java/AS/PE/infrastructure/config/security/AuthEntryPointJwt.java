package AS.PE.infrastructure.config.security; // Define el paquete para las utilidades JWT.

import jakarta.servlet.ServletException; // Importa ServletException para manejar errores de servlet.
import jakarta.servlet.http.HttpServletRequest; // Importa HttpServletRequest para manejar peticiones HTTP.
import jakarta.servlet.http.HttpServletResponse; // Importa HttpServletResponse para manejar respuestas HTTP.
import org.slf4j.Logger; // Importa Logger para registrar mensajes.
import org.slf4j.LoggerFactory; // Importa LoggerFactory para obtener una instancia de Logger.
import org.springframework.security.core.AuthenticationException; // Importa AuthenticationException de Spring Security.
import org.springframework.security.web.AuthenticationEntryPoint; // Importa AuthenticationEntryPoint para manejar entradas de autenticación.
import org.springframework.stereotype.Component; // Importa @Component para que Spring detecte esta clase como un bean.

import java.io.IOException; // Importa IOException para manejar errores de entrada/salida.

@Component // Marca esta clase como un componente de Spring.
public class AuthEntryPointJwt implements AuthenticationEntryPoint { // Implementa AuthenticationEntryPoint para manejar errores de autenticación.

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class); // Logger para registrar eventos y errores.

    @Override // Sobrescribe el mét0do commence para manejar el inicio de la autenticación.
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException { // Mét0do que se invoca cuando un usuario no autenticado intenta acceder a un recurso protegido.
        logger.error("Unauthorized error: {}", authException.getMessage()); // Registra el error de no autorizado.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized"); // Envía una respuesta HTTP 401 (No autorizado) al cliente.
    }
}