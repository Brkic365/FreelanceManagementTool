package hr.tvz.java.freelance.freelancemanagementtool.repository;

import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.AuditLog;
import hr.tvz.java.freelance.freelancemanagementtool.model.Entity;
import hr.tvz.java.freelance.freelancemanagementtool.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * An abstract base class for repositories to reduce boilerplate code for auditing.
 * It provides a shared logger instance and an AuditLogRepository instance.
 *
 * @param <T> The entity type the repository manages.
 */
public abstract class BaseRepository<T extends Entity> implements CrudRepository<T, Long> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final AuditLogRepository auditLogRepository = new AuditLogRepository();

    /**
     * Provides the name of the entity for audit logging purposes.
     * @return The entity name (e.g., "Project", "Client").
     */
    protected abstract String getEntityName();

    /**
     * Helper method to safely retrieve the old state of an entity for auditing.
     * @param id The ID of the entity to find.
     * @return A string representation of the old entity, or a placeholder if not found.
     */
    protected String findOldValue(Long id) {
        try {
            return findById(id).map(T::toString).orElse("N/A (not found)");
        } catch (DatabaseReadException e) {
            logger.warn("Could not retrieve old state for {} ID {} for audit. Log will be incomplete.", getEntityName(), id, e);
            return "N/A (read error)";
        }
    }

    /**
     * Centralized method for creating and saving an audit log entry.
     * @param oldValue The old value of the entity as a string.
     * @param newValue The new value of the entity as a string.
     */
    protected void logAudit(String oldValue, String newValue) {
        AuditLog log = new AuditLog(LocalDateTime.now(), SessionManager.getCurrentUserRole(), getEntityName(), oldValue, newValue);
        auditLogRepository.save(log);
        logger.info("Audit log created for {} action.", getEntityName());
    }
}