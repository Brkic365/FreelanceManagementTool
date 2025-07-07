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
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Handles user data operations, primarily for authentication from a text file.
 */
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static final String USERS_FILE_PATH = "data/users.txt";
    private static final int LINES_PER_RECORD = 4;

    /**
     * A record to temporarily hold user data read from the file.
     */
    private record UserRecord(long id, String username, String hash, UserRole role) {}

    /**
     * Authenticates a user based on username and password.
     */
    public Pair<Long, UserRole> authenticate(String username, String password) throws UserNotFoundException {
        logger.info("Attempting to authenticate user: {}", username);

        UserRecord userRecord = findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password."));

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), userRecord.hash());

        if (result.verified) {
            logger.info("User {} authenticated successfully as {}", username, userRecord.role());
            return new Pair<>(userRecord.id(), userRecord.role());
        } else {
            logger.warn("Authentication failed for user {}: Incorrect password.", username);
            throw new UserNotFoundException("Invalid username or password.");
        }
    }

    /**
     * Finds a user record in the text file by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the UserRecord if found.
     */
    private java.util.Optional<UserRecord> findUserByUsername(String username) {
        try {
            List<String> lines = Files.readAllLines(Path.of(USERS_FILE_PATH));
            List<String> dataLines = lines.stream().filter(line -> !line.equals("--")).toList();

            if (dataLines.size() % LINES_PER_RECORD != 0) {
                throw new ConfigurationException("User data file is corrupted.", null);
            }

            for (int i = 0; i < dataLines.size(); i += LINES_PER_RECORD) {
                if (dataLines.get(i + 1).equals(username)) {
                    UserRecord userRecordTemp = new UserRecord(
                            Long.parseLong(dataLines.get(i)),
                            dataLines.get(i + 1),
                            dataLines.get(i + 2),
                            UserRole.valueOf(dataLines.get(i + 3))
                    );
                    return java.util.Optional.of(userRecordTemp);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationException("User data file is missing or unreadable.", e);
        }
        return java.util.Optional.empty();
    }

    /**
     * Appends a new user's details to the users.txt file.
     */
    public synchronized void appendUserToTextFile(User user, String hashedPassword) {
        try {
            long fileSize = Files.exists(Path.of(USERS_FILE_PATH)) ? Files.size(Path.of(USERS_FILE_PATH)) : 0;
            String userRecord = String.format("%d%n%s%n%s%n%s",
                    user.getId(), user.getUsername(), hashedPassword, user.getRole());

            if (fileSize > 0) {
                userRecord = "\n--\n" + userRecord;
            }

            Files.writeString(Path.of(USERS_FILE_PATH), userRecord,
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            logger.info("Successfully appended new user '{}' to {}", user.getUsername(), USERS_FILE_PATH);
        } catch (IOException e) {
            logger.error("Failed to write new user to text file.", e);
        }
    }
}