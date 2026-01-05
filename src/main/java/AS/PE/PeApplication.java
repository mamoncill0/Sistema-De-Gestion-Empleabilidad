package AS.PE;

import AS.PE.domain.model.enums.Role;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.RoleEntity;
import AS.PE.infrastructure.adapters.port.out.persistence.repository.RoleJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeApplication.class, args);
	}

	@Bean
	public CommandLineRunner initRoles(RoleJpaRepository roleJpaRepository) {
		return args -> {
			// Verificar y crear el rol USER si no existe
			if (roleJpaRepository.findByName(Role.USER).isEmpty()) {
				roleJpaRepository.save(new RoleEntity(Role.USER));
				System.out.println("Role USER created.");
			}

			// Verificar y crear el rol ADMIN si no existe
			if (roleJpaRepository.findByName(Role.ADMIN).isEmpty()) {
				roleJpaRepository.save(new RoleEntity(Role.ADMIN));
				System.out.println("Role ADMIN created.");
			}
		};
	}
}
