package hr.tvz.java.freelance.freelancemanagementtool.repository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;
import hr.tvz.java.freelance.freelancemanagementtool.exception.ConfigurationException;
import hr.tvz.java.freelance.freelancemanagementtool.exception.UserNotFoundException;
import hr.tvz.java.freelance.freelancemanagementtool.model.User;
import hr.tvz.java.freelance.freelancemanagementtool.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.StandardOpenOption;

/**
 * Handles user data operations, primarily for authentication from a text file.
 */
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final String USERS_FILE_PATH = "data/users.txt";
    private static final int LINES_PER_USER_RECORD = 4; // id, username, hash, role

    /**
     * Authenticates a user based on username and password against the users.txt file.
     *
     * @param username The user's username.
     * @param password The user's plain-text password.
     * @return A Pair containing the user's ID and their role (as UserRole enum).
     * @throws UserNotFoundException if the username is not found or password does not match.
     */
    public Pair<Long, UserRole> authenticate(String username, String password) throws UserNotFoundException {
        logger.info("Attempting to authenticate user: {}", username);
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(USERS_FILE_PATH));
        } catch (IOException e) {
            logger.error("Could not read users file at path: {}", USERS_FILE_PATH, e);
            throw new ConfigurationException("User data file is missing or unreadable.", e);
        }

        // Remove all separator lines to get a clean list of data
        List<String> dataLines = lines.stream()
                .filter(line -> !line.equals("--"))
                .toList();

        // Each user record has 4 lines of data
        int linesPerRecord = 4;
        if (dataLines.size() % linesPerRecord != 0) {
            logger.error("User data file is corrupted. The number of data lines ({}) is not a multiple of {}.", dataLines.size(), linesPerRecord);
            throw new ConfigurationException("User data file is corrupted.", null);
        }

        for (int i = 0; i < dataLines.size(); i += linesPerRecord) {
            String storedUsername = dataLines.get(i + 1);

            if (storedUsername.equals(username)) {
                String storedHash = dataLines.get(i + 2);
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);

                if (result.verified) {
                    Long id = Long.parseLong(dataLines.get(i));
                    UserRole role = UserRole.valueOf(dataLines.get(i + 3));
                    logger.info("User {} authenticated successfully as {}", username, role);
                    return new Pair<>(id, role);
                } else {
                    logger.warn("Authentication failed for user {}: Incorrect password.", username);
                    throw new UserNotFoundException("Invalid username or password.");
                }
            }
        }

        logger.warn("Authentication failed for user {}: User not found.", username);
        throw new UserNotFoundException("Invalid username or password.");
    }

    /**
     * Appends a new user's details to the users.txt file.
     *
     * @param user The User object containing the new user's data.
     * @param hashedPassword The bcrypt-hashed password.
     */
    public void appendUserToTextFile(User user, String hashedPassword) {
        synchronized (this) {
            try {
                // Read all bytes to check if the file is empty or not
                long fileSize = Files.size(Path.of(USERS_FILE_PATH));

                // The record to be written
                String userRecord = new StringBuilder()
                        .append(user.getId()).append("\n")
                        .append(user.getUsername()).append("\n")
                        .append(hashedPassword).append("\n")
                        .append(user.getRole().toString())
                        .toString();

                // If the file is not empty, add a separator first.
                if (fileSize > 0) {
                    userRecord = "\n--\n" + userRecord;
                }

                Files.writeString(Path.of(USERS_FILE_PATH), userRecord, StandardOpenOption.APPEND);
                logger.info("Successfully appended new user '{}' to {}", user.getUsername(), USERS_FILE_PATH);

            } catch (IOException e) {
                logger.error("Failed to write new user to text file.", e);
            }
        }
    }
}