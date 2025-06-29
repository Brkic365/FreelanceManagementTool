package hr.tvz.java.freelance.freelancemanagementtool.model;

import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;

/**
 * Represents a user of the application.
 * Extends the base Entity class.
 */
public final class User extends Entity {
    private String username;
    private String hashedPassword;
    private UserRole role;

    /**
     * Private constructor for the Builder pattern, used for creating objects from database data.
     *
     * @param id The user's unique ID.
     * @param username The user's username.
     * @param hashedPassword The user's already hashed password.
     * @param role The user's role.
     */
    private User(long id, String username, String hashedPassword, UserRole role) {
        super(id);
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    /**
     * Constructor for creating a new user object before it is saved to the database.
     * The ID is not yet known, and the password is in plain text.
     *
     * @param username The user's username.
     * @param plainTextPassword The user's password in plain text.
     * @param role The user's role.
     */
    public User(String username, String plainTextPassword, UserRole role) {
        super(0L); // Use a temporary ID of 0; the database will generate the real one.
        this.username = username;
        this.hashedPassword = plainTextPassword; // Store plain text temporarily; repository will hash it.
        this.role = role;
    }


    // --- Getters ---
    public String getUsername() { return username; }
    public String getHashedPassword() { return hashedPassword; }
    public UserRole getRole() { return role; }


    /**
     * Builder pattern for creating User objects, typically from database records.
     */
    public static class Builder {
        private long id;
        private String username;
        private String hashedPassword;
        private UserRole role;

        public Builder(long id) {
            this.id = id;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withHashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }

        public Builder withRole(UserRole role) {
            this.role = role;
            return this;
        }

        /**
         * Builds and returns a new User object from the builder's state.
         * @return A new, configured User instance.
         */
        public User build() {
            return new User(id, username, hashedPassword, role);
        }
    }
}