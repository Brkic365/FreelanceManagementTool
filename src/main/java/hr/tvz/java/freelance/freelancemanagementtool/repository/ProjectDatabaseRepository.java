package hr.tvz.java.freelance.freelancemanagementtool.repository;

import hr.tvz.java.freelance.freelancemanagementtool.database.DatabaseConnection;
import hr.tvz.java.freelance.freelancemanagementtool.enums.ProjectStatus;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.AuditLog;
import hr.tvz.java.freelance.freelancemanagementtool.model.Project;
import hr.tvz.java.freelance.freelancemanagementtool.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the CrudRepository for Project entities, using a JDBC connection to a SQL database.
 */
public class ProjectDatabaseRepository implements CrudRepository<Project, Long> {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDatabaseRepository.class);
    private final AuditLogRepository auditLogRepository = new AuditLogRepository();

    /**
     * Maps a ResultSet row to a Project object.
     */
    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        return new Project.Builder(rs.getLong("id"))
                .withName(rs.getString("name"))
                .withDescription(rs.getString("description"))
                .withClientId(rs.getLong("client_id"))
                .withAssignedUserId(rs.getLong("assigned_user_id"))
                .withStartDate(rs.getDate("start_date").toLocalDate())
                .withDeadline(rs.getDate("deadline").toLocalDate())
                .withBudget(rs.getBigDecimal("budget"))
                .withStatus(ProjectStatus.valueOf(rs.getString("status")))
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> findAll() throws DatabaseReadException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM PROJECTS";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to fetch all projects from database.";
            logger.error(errorMessage, e);
            throw new DatabaseReadException(errorMessage, e);
        }
        return projects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Project> findById(Long id) throws DatabaseReadException {
        // Implementation is the same as before...
        String sql = "SELECT * FROM PROJECTS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProject(rs));
                }
            }
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to find project by ID: " + id;
            logger.error(errorMessage, e);
            throw new DatabaseReadException(errorMessage, e);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     * This method saves a new project to the database.
     */
    @Override
    public Project save(Project project) {
        String sql = "INSERT INTO PROJECTS (name, description, client_id, assigned_user_id, start_date, deadline, budget, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setLong(3, project.getClientId());
            stmt.setLong(4, SessionManager.getCurrentUserId()); // Assign to current user
            stmt.setDate(5, Date.valueOf(project.getStartDate()));
            stmt.setDate(6, Date.valueOf(project.getDeadline()));
            stmt.setBigDecimal(7, project.getBudget());
            stmt.setString(8, project.getStatus().toString());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                    logger.info("Successfully saved new project with ID: {}", project.getId());
                    // Guideline #9: Log the change
                    AuditLog log = new AuditLog(LocalDateTime.now(), SessionManager.getCurrentUserRole(), "Project", "N/A", project.toString());
                    auditLogRepository.save(log);
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to save project: {}", project.getName(), e);
            // In a real app, you would re-throw a custom exception here
        }
        return project;
    }

    /**
     * {@inheritDoc}
     * This method updates an existing project in the database.
     */
    @Override
    public Project update(Project project) {
        Optional<Project> oldProjectOpt = Optional.empty();
        try {
            oldProjectOpt = findById(project.getId());
        } catch (DatabaseReadException e) {
            logger.error("Could not find old project version for audit log.", e);
        }

        String sql = "UPDATE PROJECTS SET name = ?, description = ?, client_id = ?, start_date = ?, deadline = ?, budget = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setLong(3, project.getClientId());
            stmt.setDate(4, Date.valueOf(project.getStartDate()));
            stmt.setDate(5, Date.valueOf(project.getDeadline()));
            stmt.setBigDecimal(6, project.getBudget());
            stmt.setString(7, project.getStatus().toString());
            stmt.setLong(8, project.getId());
            stmt.executeUpdate();
            logger.info("Successfully updated project with ID: {}", project.getId());

            // Guideline #9: Log the change with old and new values
            String oldValue = oldProjectOpt.map(Project::toString).orElse("N/A");
            AuditLog log = new AuditLog(LocalDateTime.now(), SessionManager.getCurrentUserRole(), "Project", oldValue, project.toString());
            auditLogRepository.save(log);

        } catch (SQLException | IOException e) {
            logger.error("Failed to update project with ID: {}", project.getId(), e);
        }
        return project;
    }

    /**
     * {@inheritDoc}
     * This method deletes a project from the database by its ID.
     */
    @Override
    public void deleteById(Long id) {
        Optional<Project> projectToDeleteOpt = Optional.empty();
        try {
            projectToDeleteOpt = findById(id);
        } catch (DatabaseReadException e) {
            logger.error("Could not find project to be deleted for audit log.", e);
        }

        String sql = "DELETE FROM PROJECTS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Successfully deleted project with ID: {}", id);
                // Guideline #9: Log the change
                String oldValue = projectToDeleteOpt.map(Project::toString).orElse("N/A");
                AuditLog log = new AuditLog(LocalDateTime.now(), SessionManager.getCurrentUserRole(), "Project", oldValue, "DELETED");
                auditLogRepository.save(log);
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to delete project with ID: {}", id, e);
        }
    }
}