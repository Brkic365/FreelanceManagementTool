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
public class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final String DB_PROPERTIES_FILE = "src/main/resources/database.properties";
    private static Connection connection = null;

    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseConnection() {}

    /**
     * Establishes and returns a connection to the database.
     * If a connection already exists, it returns the existing one.
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     * @throws IOException if the properties file cannot be read.
     */
    public static synchronized Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties props = new Properties();
                props.load(new FileReader(DB_PROPERTIES_FILE));

                connection = DriverManager.getConnection(
                        props.getProperty("databaseUrl"),
                        props.getProperty("username"),
                        props.getProperty("password"));
                logger.info("Successfully connected to the database.");
            } catch (IOException e) {
                logger.error("Failed to read database properties file.", e);
                throw e;
            } catch (SQLException e) {
                logger.error("Failed to establish database connection.", e);
                throw e;
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
                logger.info("Database connection closed.");
            } catch (SQLException e) {
                logger.error("Failed to close database connection.", e);
            } finally {
                connection = null;
            }
        }
    }
}