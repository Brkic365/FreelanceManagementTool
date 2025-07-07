package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.FreelanceManagementApplication;
import hr.tvz.java.freelance.freelancemanagementtool.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for the main application window that contains the menu bar and hosts other views.
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private BorderPane mainBorderPane;
    @FXML private Menu adminMenu;
    @FXML private Label welcomeLabel;

    /**
     * Initializes the controller. Configures the UI based on the logged-in user's role.
     */
    @FXML
    public void initialize() {
        if (SessionManager.isAdmin()) {
            welcomeLabel.setText("Welcome, Admin!");
            adminMenu.setVisible(true);
        } else {
            welcomeLabel.setText("Welcome, Freelancer!");
            adminMenu.setVisible(false);
        }
        logger.info("Main view initialized for user role: {}", SessionManager.getCurrentUserRole());
    }

    /**
     * Handles the 'Logout' menu item action.
     */
    @FXML
    public void logout() {
        SessionManager.logout();
        logger.info("User logged out.");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FreelanceManagementApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            FreelanceManagementApplication.getMainStage().setTitle("Login - Freelance Tool");
            FreelanceManagementApplication.getMainStage().setScene(scene);
        } catch (IOException e) {
            logger.error("Failed to load login-view.fxml on logout.", e);
        }
    }

    /**
     * Loads and displays the project search view.
     */
    @FXML
    private void showProjects() {
        loadView("project-search-view.fxml", "Project Search");
    }

    /**
     * Loads and displays the client search view.
     */
    @FXML
    private void showClients() {
        loadView("client-search-view.fxml", "Client Search");
    }

    /**
     * Loads and displays the audit log view (Admin only).
     */
    @FXML
    public void showAuditLog() {
        if (SessionManager.isAdmin()) {
            loadView("audit-log-view.fxml", "Audit Log");
        }
    }

    /**
     * Loads and displays the user management view (Admin only).
     */
    @FXML
    private void showUserManagement() {
        if (SessionManager.isAdmin()) {
            loadView("user-management-view.fxml", "User Management");
        }
    }

    /**
     * A generic helper method to load an FXML view into the center of the BorderPane.
     *
     * @param fxmlFileName The name of the FXML file to load.
     * @param viewName A friendly name for logging purposes.
     */
    private void loadView(String fxmlFileName, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(FreelanceManagementApplication.class.getResource(fxmlFileName));
            VBox view = loader.load();
            mainBorderPane.setCenter(view);
            logger.info("Navigated to {} View.", viewName);
        } catch (IOException e) {
            logger.error("Failed to load {}. Check file name and location.", fxmlFileName, e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load the " + viewName + " screen.");
            alert.setContentText("The file '" + fxmlFileName + "' might be missing or corrupted.");
            alert.showAndWait();
        }
    }
}