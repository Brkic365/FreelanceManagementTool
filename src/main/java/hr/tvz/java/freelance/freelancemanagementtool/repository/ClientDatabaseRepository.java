package hr.tvz.java.freelance.freelancemanagementtool.repository;

import hr.tvz.java.freelance.freelancemanagementtool.database.DatabaseConnection;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Client;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the CrudRepository for Client entities.
 * It extends the BaseRepository and provides the specific SQL queries and mapping logic for Clients.
 */
public class ClientDatabaseRepository extends BaseRepository<Client> {

    /**
     * Overrides the base repository function and returns "Client" as the entity name
     *
     * @return Entity name string
     */
    @Override
    protected String getEntityName() {
        return "Client";
    }

    /**
     * Converts a result set to a client object
     *
     * @param rs Result set containing the client values
     * @return Client object
     * @throws SQLException SQL Exception
     */
    private Client mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Client(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("contact_person")
        );
    }

    /**
     * Finds all clients
     *
     * @return List of all clients
     * @throws DatabaseReadException Custom database exception
     */
    @Override
    public List<Client> findAll() throws DatabaseReadException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, name, email, contact_person FROM CLIENTS ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapResultSetToEntity(rs));
            }
            logger.info("Successfully retrieved {} clients from the database.", clients.size());
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to fetch all clients from database.";
            throw new DatabaseReadException(errorMessage, e);
        }
        return clients;
    }

    /**
     * Find the client by ID
     *
     * @param id The ID of the entity to retrieve.
     * @return Optional value of the client
     * @throws DatabaseReadException Custom database exception
     */
    @Override
    public Optional<Client> findById(Long id) throws DatabaseReadException {
        String sql = "SELECT id, name, email, contact_person FROM CLIENTS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to find client by ID: " + id;
            throw new DatabaseReadException(errorMessage, e);
        }
        return Optional.empty();
    }

    /**
     * Saves the client to database
     *
     * @param client The entity to save.
     * @return Client object
     */
    @Override
    public Client save(Client client) {
        String sql = "INSERT INTO CLIENTS (name, email, contact_person) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getContactPerson());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                    logAudit("N/A", client.toString());
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to save client: {}", client.getName(), e);
        }
        return client;
    }

    /**
     * Updates the Client in the database
     *
     * @param client The entity with updated information.
     * @return Client object
     */
    @Override
    public Client update(Client client) {
        String oldValue = findOldValue(client.getId());
        String sql = "UPDATE CLIENTS SET name = ?, email = ?, contact_person = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getContactPerson());
            stmt.setLong(4, client.getId());
            stmt.executeUpdate();
            logAudit(oldValue, client.toString());
        } catch (SQLException | IOException e) {
            logger.error("Failed to update client with ID: {}", client.getId(), e);
        }
        return client;
    }

    /**
     * Deletes the client using his ID
     *
     * @param id The ID of the entity to delete.
     */
    @Override
    public void deleteById(Long id) {
        String oldValue = findOldValue(id);
        String sql = "DELETE FROM CLIENTS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() > 0) {
                logAudit(oldValue, "DELETED");
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to delete client with ID: {}", id, e);
        }
    }
}