package hr.tvz.java.freelance.freelancemanagementtool.repository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import hr.tvz.java.freelance.freelancemanagementtool.database.DatabaseConnection;
import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.User;
import hr.tvz.java.freelance.freelancemanagementtool.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the CrudRepository for User entities, using a JDBC connection.
 * Handles all database operations for users.
 */
public class UserDatabaseRepository implements CrudRepository<User, Long> {

    private static final Logger logger = LoggerFactory.getLogger(UserDatabaseRepository.class);

    /**
     * Converts a result set to a user object
     *
     * @param rs Result set containing the user values
     * @return User object
     * @throws SQLException SQL Exception
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User.Builder(rs.getLong("id"))
                .withUsername(rs.getString("username"))
                .withHashedPassword(rs.getString("hashed_password"))
                .withRole(UserRole.valueOf(rs.getString("role")))
                .build();
    }

    /**
     * Finds all users
     *
     * @return List of all users
     * @throws DatabaseReadException Custom database exception
     */
    @Override
    public List<User> findAll() throws DatabaseReadException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, hashed_password, role FROM USERS";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException | IOException e) {
            throw new DatabaseReadException("Failed to fetch all users.", e);
        }
        return users;
    }

    /**
     * Saves the user to the database
     *
     * @param user The entity to save.
     * @return User object
     */
    @Override
    public User save(User user) {
        return saveAndReturnHashedPassword(user).getKey();
    }

    /**
     * Deletes the user using his ID
     *
     * @param id The ID of the entity to delete.
     */
    @Override
    public void deleteById(Long id) {
        if (id == 1L) {
            logger.warn("Attempted to delete the primary admin user (ID 1). Operation blocked.");
            return;
        }
        String sql = "DELETE FROM USERS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            logger.info("Deleted user with ID: {}", id);
        } catch (SQLException | IOException e) {
            logger.error("Failed to delete user with ID: {}", id, e);
        }
    }

    /**
     * Minimal function to satisfy CRUD requirements
     *
     * @param id The ID of the entity to retrieve.
     * @return Empty Optional
     */
    @Override public Optional<User> findById(Long id) { return Optional.empty(); }

    /**
     * Minimal function to satisfy CRUD requirements
     *
     * @param entity The entity with updated information.
     * @return Null value
     */
    @Override public User update(User entity) { return null; }

    /**
     * Saves the user to database and returns the Pair object with user and hashed password
     *
     * @param user User object
     * @return Pair with user as key and hashed password as value
     */
    public Pair<User, String> saveAndReturnHashedPassword(User user) {
        String hashedPassword = BCrypt.withDefaults().hashToString(12, user.getHashedPassword().toCharArray());
        String sql = "INSERT INTO USERS (username, hashed_password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getRole().toString());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    logger.info("Saved new user '{}' with ID: {}", user.getUsername(), user.getId());
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to save user.", e);
        }
        return new Pair<>(user, hashedPassword);
    }
}