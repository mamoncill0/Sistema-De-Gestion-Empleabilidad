package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.port.out.CurrentUserPort;
import AS.PE.infrastructure.adapters.port.out.persistence.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserAdapter implements CurrentUserPort {

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            UserEntity userDetails = (UserEntity) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new IllegalStateException("No authenticated user found");
    }
}