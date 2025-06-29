package hr.tvz.java.freelance.freelancemanagementtool.repository;

import hr.tvz.java.freelance.freelancemanagementtool.database.DatabaseConnection;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the CrudRepository for Client entities, using a JDBC connection.
 */
public class ClientDatabaseRepository implements CrudRepository<Client, Long> {

    private static final Logger logger = LoggerFactory.getLogger(ClientDatabaseRepository.class);

    /**
     * Maps a ResultSet row to a Client object.
     * @param rs The ResultSet from which to map data.
     * @return A new Client object.
     * @throws SQLException if a database access error occurs.
     */
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("contact_person")
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll() throws DatabaseReadException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTS";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
            logger.info("Successfully retrieved {} clients from the database.", clients.size());
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to fetch all clients from database.";
            logger.error(errorMessage, e);
            throw new DatabaseReadException(errorMessage, e);
        }
        return clients;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Client> findById(Long id) throws DatabaseReadException {
        String sql = "SELECT * FROM CLIENTS WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException | IOException e) {
            String errorMessage = "Failed to find client by ID: " + id;
            logger.error(errorMessage, e);
            throw new DatabaseReadException(errorMessage, e);
        }
        return Optional.empty();
    }

    // We will implement save, update, delete later if we build a Client management screen.
    @Override public Client save(Client entity) { return null; }
    @Override public void deleteById(Long id) { }
    @Override public Client update(Client entity) { return null; }
}