package hr.tvz.java.freelance.freelancemanagementtool.repository;

import hr.tvz.java.freelance.freelancemanagementtool.database.DatabaseConnection;
import hr.tvz.java.freelance.freelancemanagementtool.enums.ProjectStatus;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Project;
import hr.tvz.java.freelance.freelancemanagementtool.session.SessionManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the CrudRepository for Project entities.
 * It extends the BaseRepository and provides the specific SQL queries and mapping logic for Projects.
 */
public class ProjectDatabaseRepository extends BaseRepository<Project> {

    /**
     * Overrides the base repository function and returns "Client" as the entity name
     *
     * @return Entity name string
     */
    @Override
    protected String getEntityName() {
        return "Project";
    }

    /**
     * Converts a result set to a project object
     *
     * @param rs Result set containing the project values
     * @return Project object
     * @throws SQLException SQL Exception
     */
    private Project mapResultSetToEntity(ResultSet rs) throws SQLException {
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
     * Finds all projects
     *
     * @return List of all projects
     * @throws DatabaseReadException Custom database exception
     */
    @Override
    public List<Project> findAll() throws DatabaseReadException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT id, name, description, client_id, assigned_user_id, start_date, deadline, budget, status FROM PROJECTS";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                projects.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to fetch all projects from database.";
            throw new DatabaseReadException(errorMessage, e);
        }
        return projects;
    }

    /**
     * Find the project by ID
     *
     * @param id The ID of the entity to retrieve.
     * @return Optional value of the project
     * @throws DatabaseReadException Custom database exception
     */
    @Override
    public Optional<Project> findById(Long id) throws DatabaseReadException {
        String sql = "SELECT id, name, description, client_id, assigned_user_id, start_date, deadline, budget, status FROM PROJECTS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to find project by ID: " + id;
            throw new DatabaseReadException(errorMessage, e);
        }
        return Optional.empty();
    }

    /**
     * Saves the project to database
     *
     * @param project The entity to save.
     * @return Project object
     */
    @Override
    public Project save(Project project) {
        String sql = "INSERT INTO PROJECTS (name, description, client_id, assigned_user_id, start_date, deadline, budget, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setLong(3, project.getClientId());
            stmt.setLong(4, SessionManager.getCurrentUserId());
            stmt.setDate(5, Date.valueOf(project.getStartDate()));
            stmt.setDate(6, Date.valueOf(project.getDeadline()));
            stmt.setBigDecimal(7, project.getBudget());
            stmt.setString(8, project.getStatus().toString());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                    logAudit("N/A", project.toString());
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to save project: {}", project.getName(), e);
        }
        return project;
    }

    /**
     * Updates the Project in the database
     *
     * @param project The entity with updated information.
     * @return Project object
     */
    @Override
    public Project update(Project project) {
        String oldValue = findOldValue(project.getId());
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
            logAudit(oldValue, project.toString());
        } catch (SQLException | IOException e) {
            logger.error("Failed to update project with ID: {}", project.getId(), e);
        }
        return project;
    }

    /**
     * Deletes the project using his ID
     *
     * @param id The ID of the entity to delete.
     */
    @Override
    public void deleteById(Long id) {
        String oldValue = findOldValue(id);
        String sql = "DELETE FROM PROJECTS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() > 0) {
                logAudit(oldValue, "DELETED");
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to delete project with ID: {}", id, e);
        }
    }
}