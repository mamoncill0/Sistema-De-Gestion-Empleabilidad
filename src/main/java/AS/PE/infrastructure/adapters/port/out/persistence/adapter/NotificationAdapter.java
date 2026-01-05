package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.port.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(NotificationAdapter.class);

    @Override
    public void notify(String message) {
        // Implementación simple: registrar en logs
        // En producción, esto podría enviar emails, SMS, push notifications, etc.
        logger.info("[NOTIFICATION] {}", message);

        // Ejemplo: enviar email
        // emailService.send(message);

        // Ejemplo: enviar notificación push
        // pushNotificationService.send(message);
    }
}