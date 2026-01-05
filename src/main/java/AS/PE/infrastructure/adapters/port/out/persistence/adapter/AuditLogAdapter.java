package AS.PE.infrastructure.adapters.port.out.persistence.adapter;

import AS.PE.domain.port.out.AuditLogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditLogAdapter implements AuditLogPort {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogAdapter.class);

    @Override
    public void register(String action, Long entityId, String entityType) {
        // Implementación simple: registrar en logs
        // En producción, esto podría guardar en una tabla de auditoría
        String auditMessage = String.format("[AUDIT] %s - Action: %s | Entity: %s | ID: %d",
                LocalDateTime.now(), action, entityType, entityId);
        logger.info(auditMessage);

        // Aquí podrías guardar en una tabla audit_logs si lo deseas
        // AuditLogEntity audit = new AuditLogEntity(action, entityId, entityType, LocalDateTime.now());
        // auditLogRepository.save(audit);
    }
}