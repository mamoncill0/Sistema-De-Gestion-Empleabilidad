package AS.PE.infrastructure.config.security; // Define el paquete para las utilidades JWT.

import io.jsonwebtoken.*; // Importa todas las clases necesarias de jjwt.
import io.jsonwebtoken.io.Decoders; // Importa Decoders para decodificar la clave secreta.
import io.jsonwebtoken.security.Keys; // Importa Keys para generar claves seguras.
import org.slf4j.Logger; // Importa la interfaz Logger para registrar mensajes.
import org.slf4j.LoggerFactory; // Importa LoggerFactory para obtener una instancia de Logger.
import org.springframework.beans.factory.annotation.Value; // Importa @Value para inyectar valores de propiedades.
import org.springframework.security.core.Authentication; // Importa Authentication de Spring Security.
import org.springframework.security.core.userdetails.UserDetails; // Importa UserDetails de Spring Security.
import org.springframework.stereotype.Component; // Importa @Component para que Spring detecte esta clase como un bean.

import java.security.Key; // Importa Key para manejar claves de seguridad.
import java.util.Date; // Importa Date para manejar fechas y tiempos.

@Component // Marca esta clase como un componente de Spring para que pueda ser inyectada.
public class JwtProvider { // Clase para la generación y validación de tokens JWT.

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class); // Logger para registrar eventos y errores.

    @Value("${application.security.jwt.secret-key}") // CORRECCIÓN: Inyecta el valor de la propiedad 'application.security.jwt.secret-key'.
    private String jwtSecret; // Clave secreta para firmar y verificar tokens.

    @Value("${application.security.jwt.expiration}") // CORRECCIÓN: Inyecta el valor de la propiedad 'application.security.jwt.expiration'.
    private int jwtExpirationMs; // Tiempo de expiración del token en milisegundos.

    // Mét0do para generar un token JWT a partir de la autenticación del usuario.
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal(); // Obtiene el objeto UserDetails del principal autenticado.

        return Jwts.builder() // Inicia la construcción de un nuevo token JWT.
                .setSubject((userPrincipal.getUsername())) // Establece el nombre de usuario como el "subject" del token.
                .setIssuedAt(new Date()) // Establece la fecha de emisión del token a la fecha actual.
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Establece la fecha de expiración del token.
                .signWith(key(), SignatureAlgorithm.HS512) // Firma el token con la clave secreta y el algoritmo HS512.
                .compact(); // Compacta el token en su formato final de cadena.
    }

    // Mét0do privado para obtener la clave de firma a partir del secreto.
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); // Decodifica el secreto Base64 y genera una clave HMAC.
    }

    // Mét0do para obtener el nombre de usuario del token JWT.
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build() // Construye un parser JWT con la clave de firma.
                .parseClaimsJws(token).getBody().getSubject(); // Parsea el token, obtiene el cuerpo y extrae el "subject" (nombre de usuario).
    }

    // Mét0do para validar un token JWT.
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken); // Intenta parsear el token con la clave.
            return true; // Si no hay excepción, el token es válido.
        } catch (MalformedJwtException e) { // Captura excepción si el token está mal formado.
            logger.error("Invalid JWT token: {}", e.getMessage()); // Registra el error.
        } catch (ExpiredJwtException e) { // Captura excepción si el token ha expirado.
            logger.error("JWT token is expired: {}", e.getMessage()); // Registra el error.
        } catch (UnsupportedJwtException e) { // Captura excepción si el token no es soportado.
            logger.error("JWT token is unsupported: {}", e.getMessage()); // Registra el error.
        } catch (IllegalArgumentException e) { // Captura excepción si el token está vacío o nulo.
            logger.error("JWT claims string is empty: {}", e.getMessage()); // Registra el error.
        }
        return false; // Si ocurre alguna excepción, el token no es válido.
    }
}