// File: exception/ConfigurationException.java
package hr.tvz.java.freelance.freelancemanagementtool.exception;

/**
 * An unchecked exception thrown when there is a critical configuration error,
 * such as a missing file.
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}