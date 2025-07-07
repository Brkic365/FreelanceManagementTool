package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Client;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ClientDatabaseRepository;
import hr.tvz.java.freelance.freelancemanagementtool.session.SessionManager;
import hr.tvz.java.freelance.freelancemanagementtool.util.DialogHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Client Search screen. Handles displaying, filtering,
 * and managing client data.
 */
public class ClientSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ClientSearchController.class);

    @FXML private TextField nameFilterField;
    @FXML private TableView<Client> clientsTableView;
    @FXML private TableColumn<Client, String> nameColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> contactPersonColumn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final ClientDatabaseRepository clientRepository = new ClientDatabaseRepository();
    private List<Client> allClients = new ArrayList<>();

    /**
     * Initializes the controller when the FXML is loaded.
     * Sets up table columns, configures UI based on user role,
     * and performs the initial load of client data.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        configureRoleBasedAccess();
        loadClients();
    }

    /**
     * Configures the cell value factories for the TableView columns.
     */
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        contactPersonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContactPerson()));
    }

    /**
     * Hides or shows modification buttons based on the logged-in user's role.
     * Only Admins can add, edit, or delete clients.
     */
    private void configureRoleBasedAccess() {
        boolean isNotAdmin = !SessionManager.isAdmin();
        addButton.setManaged(!isNotAdmin);
        addButton.setVisible(!isNotAdmin);
        editButton.setManaged(!isNotAdmin);
        editButton.setVisible(!isNotAdmin);
        deleteButton.setManaged(!isNotAdmin);
        deleteButton.setVisible(!isNotAdmin);
    }

    /**
     * Loads all clients from the repository and populates the table.
     */
    private void loadClients() {
        try {
            allClients = clientRepository.findAll();
            clientsTableView.setItems(FXCollections.observableArrayList(allClients));
            logger.info("Successfully loaded {} clients into the table.", allClients.size());
        } catch (DatabaseReadException e) {
            showStyledAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Clients", "Could not retrieve client data from the database. Please check the logs.");
        }
    }

    /**
     * Handles the filter button action. Filters the client list based on name.
     */
    @FXML
    private void handleFilter() {
        String nameFilter = nameFilterField.getText();
        if (nameFilter == null || nameFilter.isBlank()) {
            clientsTableView.setItems(FXCollections.observableArrayList(allClients));
            return;
        }
        List<Client> filteredList = allClients.stream()
                .filter(c -> c.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .toList();
        clientsTableView.setItems(FXCollections.observableArrayList(filteredList));
    }

    /**
     * Clears the name filter and restores the full list of clients.
     */
    @FXML
    private void clearFilters() {
        nameFilterField.clear();
        clientsTableView.setItems(FXCollections.observableArrayList(allClients));
    }

    /**
     * Opens the client edit dialog in "Add New" mode using the generic DialogHelper.
     */
    @FXML
    private void handleAddNewClient() {
        boolean saveClicked = DialogHelper.showEditDialog("client-edit-view.fxml", "Add New Client", null);
        if (saveClicked) {
            loadClients();
        }
    }

    /**
     * Opens the client edit dialog in "Edit" mode for the selected client.
     */
    @FXML
    private void handleEditClient() {
        Client selectedClient = clientsTableView.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            boolean saveClicked = DialogHelper.showEditDialog("client-edit-view.fxml", "Edit Client", selectedClient);
            if (saveClicked) {
                loadClients();
            }
        } else {
            showStyledAlert(Alert.AlertType.WARNING, "Selection Missing", "No Client Selected", "Please select a client from the table to edit.");
        }
    }

    /**
     * Deletes the selected client after user confirmation.
     */
    @FXML
    private void handleDeleteClient() {
        Client selectedClient = clientsTableView.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            showStyledAlert(Alert.AlertType.WARNING, "Selection Missing", "No Client Selected", "Please select a client from the table to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this client?");
        confirmation.setContentText("Client: " + selectedClient.getName() + "\nThis action cannot be undone.");
        styleAlert(confirmation);

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            clientRepository.deleteById(selectedClient.getId());
            logger.info("User confirmed deletion of client ID: {}", selectedClient.getId());
            loadClients();
        }
    }

    /**
     * Creates, styles, and shows an Alert dialog.
     *
     * @param type The type of alert (e.g., WARNING, ERROR).
     * @param title The text for the window title bar.
     * @param header The main header text inside the dialog.
     * @param content The detailed content message.
     */
    private void showStyledAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Applies the custom dark theme stylesheet to any given Alert.
     *
     * @param alert The Alert dialog to be styled.
     */
    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/hr/tvz/java/freelance/freelancemanagementtool/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}