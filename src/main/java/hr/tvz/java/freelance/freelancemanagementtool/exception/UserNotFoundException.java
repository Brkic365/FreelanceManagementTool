// File: exception/UserNotFoundException.java
package hr.tvz.java.freelance.freelancemanagementtool.exception;

/**
 * A checked exception thrown when a user lookup fails to find a matching user.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}