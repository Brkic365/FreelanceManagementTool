package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.FreelanceManagementApplication;
import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;
import hr.tvz.java.freelance.freelancemanagementtool.exception.UserNotFoundException;
import hr.tvz.java.freelance.freelancemanagementtool.repository.UserRepository;
import hr.tvz.java.freelance.freelancemanagementtool.session.SessionManager;
import hr.tvz.java.freelance.freelancemanagementtool.util.Pair;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for the login screen. Handles user authentication.
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private final UserRepository userRepository = new UserRepository();

    /**
     * Handles the action of the login button. It attempts to authenticate the user
     * and, on success, navigates to the main application screen.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            // Guideline #6: Authenticate using the UserRepository which reads from a text file.
            Pair<Long, UserRole> userDetails = userRepository.authenticate(username, password);

            // Guideline #7: Store session for the authenticated user role.
            SessionManager.login(userDetails.getKey(), userDetails.getValue());
            logger.info("User '{}' with role {} logged in successfully.", username, userDetails.getValue());

            showMainScreen();

        } catch (UserNotFoundException e) {
            // Guideline #3: Catching our custom checked exception.
            logger.warn("Login failed for user '{}'. Reason: {}", username, e.getMessage());
            errorLabel.setText("Invalid username or password.");
        }
    }

    /**
     * Loads and displays the main application screen after a successful login.
     */
    private void showMainScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FreelanceManagementApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            FreelanceManagementApplication.getMainStage().setTitle("Dashboard - Freelance Tool");
            FreelanceManagementApplication.getMainStage().setScene(scene);
        } catch (IOException e) {
            // Guideline #3: Logging an exception.
            logger.error("Critical error: Failed to load main-view.fxml", e);
            errorLabel.setText("Error: Could not load the main application screen.");
        }
    }
}