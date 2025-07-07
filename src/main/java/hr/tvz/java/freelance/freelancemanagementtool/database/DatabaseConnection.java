package hr.tvz.java.freelance.freelancemanagementtool.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the connection to the database.
 * Reads connection details from a properties file.
 */
public final class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final String DB_PROPERTIES_FILE = "src/main/resources/database.properties";
    private static Connection connection = null;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseConnection() {}

    /**
     * Establishes and returns a connection to the database.
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     * @throws IOException if the properties file cannot be read.
     */
    public static synchronized Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            logger.debug("No existing connection found or connection is closed. Creating new one.");

            try (FileReader reader = new FileReader(DB_PROPERTIES_FILE)) {
                Properties props = new Properties();
                props.load(reader);
                connection = DriverManager.getConnection(
                        props.getProperty("databaseUrl"),
                        props.getProperty("username"),
                        props.getProperty("password"));
                logger.info("Successfully established a new database connection.");
            }
        }
        return connection;
    }

    /**
     * Closes the existing database connection if it is open.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed successfully.");
            } catch (SQLException e) {
                logger.error("Failed to close database connection.", e);
            }
        }
    }
}