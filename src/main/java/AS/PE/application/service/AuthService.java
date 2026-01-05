package AS.PE.application.service;

import AS.PE.domain.exception.RoleNotFoundException;
import AS.PE.domain.model.User;
import AS.PE.domain.model.enums.Role;
import AS.PE.domain.port.out.RoleRepositoryPort;
import AS.PE.domain.port.out.UserRepositoryPort;
import AS.PE.infrastructure.adapters.port.in.rest.dto.request.LoginRequest;
import AS.PE.infrastructure.adapters.port.in.rest.dto.request.RegisterRequest;
import AS.PE.infrastructure.adapters.port.in.rest.dto.response.AuthResponse;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.UserEntity;
import AS.PE.infrastructure.config.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepositoryPort userRepositoryPort,
                       RoleRepositoryPort roleRepositoryPort,
                       PasswordEncoder encoder,
                       JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateJwtToken(authentication);

            UserEntity userDetails = (UserEntity) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            return new AuthResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles.iterator().next());
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario o la contraseña es incorrecto o no existe.");
        }
    }

    public void registerUser(RegisterRequest signUpRequest) {
        // Verificamos si el username ya existe
        if (userRepositoryPort.existsByUsername(signUpRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Username is already taken!");
        }

        // Verificamos si el email ya existe
        if (userRepositoryPort.existsByEmail(signUpRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!");
        }

        // Creamos el usuario del dominio.
        // Nota: El constructor de User espera (id, username, email, password, role).
        // Pasamos null al ID porque se generará al guardar.
        // Pasamos null al Role inicialmente, lo asignaremos después.
        User user = new User(
                null,
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                null
        );

        Set<String> strRoles = signUpRequest.getRole();
        Role roleToAssign;

        if (strRoles == null || strRoles.isEmpty()) {
            roleToAssign = roleRepositoryPort.findByName(Role.USER)
                    .orElseThrow(() -> new RoleNotFoundException("Error: Role USER is not found. This is a server configuration issue."));
        } else {
            // Tomamos el primer rol de la lista (asumiendo que tu modelo User solo tiene un rol simple, no una lista)
            String roleName = strRoles.iterator().next();
            Role roleEnum;
            try {
                roleEnum = Role.valueOf(roleName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Invalid role specified: " + roleName);
            }

            roleToAssign = roleRepositoryPort.findByName(roleEnum)
                    .orElseThrow(() -> new RoleNotFoundException("Error: Role " + roleEnum + " is not found."));
        }

        user.setRole(roleToAssign);
        userRepositoryPort.save(user);
    }
}
