package hr.tvz.java.freelance.freelancemanagementtool.exception;

/**
 * A checked exception thrown when there is an error reading data from the database.
 */
public class DatabaseReadException extends Exception {
    public DatabaseReadException(String message, Throwable cause) {
        super(message, cause);
    }
}