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
     * The ID is set to 0 and the password is in plain text, since it will be updated by the repository.
     *
     * @param username The user's username.
     * @param plainTextPassword The user's password in plain text.
     * @param role The user's role.
     */
    public User(String username, String plainTextPassword, UserRole role) {
        super(0L);
        this.username = username;
        this.hashedPassword = plainTextPassword;
        this.role = role;
    }

    /**
     * Gets the user's username
     *
     * @return Username string
     */
    public String getUsername() { return username; }

    /**
     * Gets the user's hashed password
     *
     * @return Hashed password string
     */
    public String getHashedPassword() { return hashedPassword; }

    /**
     * Gets the user's role
     *
     * @return User role value
     */
    public UserRole getRole() { return role; }


    /**
     * Builder pattern for creating User objects, typically from database records.
     */
    public static class Builder {
        private long id;
        private String username;
        private String hashedPassword;
        private UserRole role;

        /**
         * Builder constructor
         *
         * @param id ID of the project
         */
        public Builder(long id) {
            this.id = id;
        }

        /**
         * Builder username setter
         *
         * @param username Username of the user
         */
        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Builder hashed password setter
         *
         * @param hashedPassword Hashed password of the user
         */
        public Builder withHashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }

        /**
         * Builder role setter
         *
         * @param role Role of the user
         */
        public Builder withRole(UserRole role) {
            this.role = role;
            return this;
        }

        /**
         * Builds and returns a new User object from the builder's state.
         *
         * @return A new, configured User instance.
         */
        public User build() {
            return new User(id, username, hashedPassword, role);
        }
    }
}