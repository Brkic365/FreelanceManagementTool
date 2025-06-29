// File: exception/DataSerializationException.java
package hr.tvz.java.freelance.freelancemanagementtool.exception;

/**
 * An unchecked exception thrown during serialization or deserialization of data.
 */
public class DataSerializationException extends RuntimeException {
    public DataSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}