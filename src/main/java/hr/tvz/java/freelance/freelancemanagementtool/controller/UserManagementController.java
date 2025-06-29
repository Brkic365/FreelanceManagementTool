package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.User;
import hr.tvz.java.freelance.freelancemanagementtool.repository.UserDatabaseRepository;
import hr.tvz.java.freelance.freelancemanagementtool.repository.UserRepository;
import hr.tvz.java.freelance.freelancemanagementtool.util.Pair;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the User Management screen.
 */
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @FXML private TableView<User> usersTableView;
    @FXML private TableColumn<User, Number> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, UserRole> roleColumn;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<UserRole> roleComboBox;

    private final UserDatabaseRepository userRepository = new UserDatabaseRepository();
    private final UserRepository textFileUserRepository = new UserRepository();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getId()));
        usernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        roleColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRole()));

        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));
        roleComboBox.setValue(UserRole.FREELANCER); // Default to safer role

        loadUsers();
    }

    /**
     * Loads users from the database into the table.
     */
    private void loadUsers() {
        try {
            List<User> users = userRepository.findAll();
            usersTableView.setItems(FXCollections.observableArrayList(users));
        } catch (DatabaseReadException e) {
            logger.error("Failed to load users for management screen.", e);
            new Alert(Alert.AlertType.ERROR, "Could not load user data.").showAndWait();
        }
    }

    /**
     * Handles adding a new user.
     */
    @FXML
    private void handleAddNewUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        UserRole role = roleComboBox.getValue();

        if (username.isBlank() || password.isBlank() || role == null) {
            new Alert(Alert.AlertType.WARNING, "All fields are required to add a new user.").show();
            return;
        }

        User newUser = new User(username, password, role);

        // Step 1: Save to the database and get back the user (with ID) and the hashed password.
        Pair<User, String> result = userRepository.saveAndReturnHashedPassword(newUser);
        User savedUser = result.getKey();
        String hashedPassword = result.getValue();

        // Step 2: If the user was saved to the DB (ID > 0), append to the text file.
        if (savedUser.getId() > 0) {
            textFileUserRepository.appendUserToTextFile(savedUser, hashedPassword);
        }

        clearInputFields();
        loadUsers(); // Refresh the table
    }

    /**
     * Handles deleting the selected user.
     */
    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a user to delete.").show();
            return;
        }

        if (selectedUser.getId() == 1L) {
            new Alert(Alert.AlertType.ERROR, "The primary admin (ID 1) cannot be deleted.").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User: " + selectedUser.getUsername());
        confirmation.setContentText("Are you sure? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userRepository.deleteById(selectedUser.getId());
            loadUsers(); // Refresh the table
        }
    }

    private void clearInputFields() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(UserRole.FREELANCER);
    }
}