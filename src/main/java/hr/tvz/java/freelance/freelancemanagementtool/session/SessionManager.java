package hr.tvz.java.freelance.freelancemanagementtool.session;

import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;

/**
 * Manages the user session throughout the application's lifecycle.
 * This class holds the state of the currently logged-in user.
 */
public final class SessionManager {

    private static Long currentUserId;
    private static UserRole currentUserRole;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SessionManager() {}

    /**
     * Logs a user in by setting their session details.
     *
     * @param userId The ID of the logged-in user.
     * @param role   The role of the logged-in user.
     */
    public static void login(Long userId, UserRole role) {
        currentUserId = userId;
        currentUserRole = role;
    }

    /**
     * Logs the current user out by clearing session details.
     */
    public static void logout() {
        currentUserId = null;
        currentUserRole = null;
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The user ID, or null if no user is logged in.
     */
    public static Long getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Retrieves the role of the currently logged-in user.
     *
     * @return The user's role, or null if no user is logged in.
     */
    public static UserRole getCurrentUserRole() {
        return currentUserRole;
    }

    /**
     * Checks if the current user has the ADMIN role.
     *
     * @return true if the user is an ADMIN, false otherwise.
     */
    public static boolean isAdmin() {
        return UserRole.ADMIN.equals(currentUserRole);
    }
}