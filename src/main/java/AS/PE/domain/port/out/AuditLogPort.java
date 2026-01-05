package AS.PE.domain.port.out;

public interface AuditLogPort {
    void register(String action, Long entityId, String entityType);
}