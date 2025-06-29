package hr.tvz.java.freelance.freelancemanagementtool.model;

import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A record to store an audit trail of changes in the system.
 * Implements Serializable to be saved to a binary file.
 * @param changedAt The timestamp of the change.
 * @param userRole The role of the user who made the change.
 * @param entityName The name of the entity that was changed (e.g., "Project").
 * @param oldValue The state of the data before the change.
 * @param newValue The state of the data after the change.
 */
public record AuditLog(
        LocalDateTime changedAt,
        UserRole userRole,
        String entityName,
        String oldValue,
        String newValue) implements Serializable {
}