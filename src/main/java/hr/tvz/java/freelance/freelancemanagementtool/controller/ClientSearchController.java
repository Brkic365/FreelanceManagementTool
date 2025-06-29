package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Client;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ClientDatabaseRepository;
import hr.tvz.java.freelance.freelancemanagementtool.session.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML private Button addButton, editButton, deleteButton;

    private final ClientDatabaseRepository clientRepository = new ClientDatabaseRepository();
    private List<Client> allClients = new ArrayList<>();

    /**
     * Initializes the controller. Sets up table columns, loads initial data,
     * and configures UI based on user role.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        configureRoleBasedAccess();
        loadClients();
    }

    /**
     * Configures cell value factories for the TableView columns.
     */
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        contactPersonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContactPerson()));
    }

    /**
     * Hides modification buttons if the logged-in user is not an Admin.
     */
    private void configureRoleBasedAccess() {
        boolean isNotAdmin = !SessionManager.isAdmin();
        addButton.setVisible(!isNotAdmin);
        editButton.setVisible(!isNotAdmin);
        deleteButton.setVisible(!isNotAdmin);
    }

    /**
     * Loads all clients from the database and populates the table.
     */
    private void loadClients() {
        try {
            allClients = clientRepository.findAll();
            clientsTableView.setItems(FXCollections.observableArrayList(allClients));
            logger.info("Successfully loaded {} clients into the table.", allClients.size());
        } catch (DatabaseReadException e) {
            logger.error("Failed to load clients from the database.", e);
            new Alert(Alert.AlertType.ERROR, "Error loading client data. Please check logs.").showAndWait();
        }
    }

    /**
     * Handles the filter button action.
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
                .collect(Collectors.toList());

        clientsTableView.setItems(FXCollections.observableArrayList(filteredList));
        logger.info("Applied client filter. Displaying {} clients.", filteredList.size());
    }

    /**
     * Clears the filter and shows all clients.
     */
    @FXML
    private void clearFilters() {
        nameFilterField.clear();
        clientsTableView.setItems(FXCollections.observableArrayList(allClients));
        logger.info("Client filters cleared.");
    }

    // Placeholder methods for Add, Edit, Delete functionality
    @FXML private void handleAddNewClient() { logger.info("Add New Client button clicked."); }
    @FXML private void handleEditClient() { logger.info("Edit Client button clicked."); }
    @FXML private void handleDeleteClient() { logger.info("Delete Client button clicked."); }
}