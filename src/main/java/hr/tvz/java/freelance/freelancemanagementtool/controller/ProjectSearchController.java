package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.enums.ProjectStatus;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Client;
import hr.tvz.java.freelance.freelancemanagementtool.model.Project;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ClientDatabaseRepository;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ProjectDatabaseRepository;
import hr.tvz.java.freelance.freelancemanagementtool.util.Config;
import hr.tvz.java.freelance.freelancemanagementtool.util.DialogHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller for the Project Search screen. Handles displaying, filtering,
 * and managing project data by interacting with repositories and dialogs.
 */
public class ProjectSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectSearchController.class);

    @FXML private TextField nameFilterField;
    @FXML private ComboBox<ProjectStatus> statusFilterComboBox;
    @FXML private TableView<Project> projectsTableView;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, String> clientColumn;
    @FXML private TableColumn<Project, String> deadlineColumn;
    @FXML private TableColumn<Project, String> statusColumn;
    @FXML private TableColumn<Project, String> budgetColumn;

    private final ProjectDatabaseRepository projectRepository = new ProjectDatabaseRepository();
    private final ClientDatabaseRepository clientRepository = new ClientDatabaseRepository();
    private List<Project> allProjects = new ArrayList<>();
    private Map<Long, Client> clientMap;

    /**
     * Initializes the controller when the FXML is loaded.
     * Sets up table columns, loads clients into a map for efficiency,
     * and performs the initial load of project data.
     */
    @FXML
    public void initialize() {
        loadClientsIntoMap();
        setupTableColumns();
        statusFilterComboBox.setItems(FXCollections.observableArrayList(ProjectStatus.values()));
        loadProjects();
    }

    /**
     * Loads all clients from the database into a Map for efficient lookup
     * when displaying project data. This avoids repeated database queries.
     */
    private void loadClientsIntoMap() {
        try {
            clientMap = clientRepository.findAll().stream()
                    .collect(Collectors.toMap(Client::getId, Function.identity()));
        } catch (DatabaseReadException e) {
            logger.error("CRITICAL: Failed to load clients for project view.", e);
            showStyledAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Client Data", "Could not establish a connection or read client data. The application may not function correctly.");
            clientMap = Map.of();
        }
    }

    /**
     * Configures the cell value factories for the TableView columns,
     * binding them to the properties of the Project model.
     */
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        deadlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        budgetColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBudget().toString()));
        clientColumn.setCellValueFactory(cellData -> {
            Client client = clientMap.get(cellData.getValue().getClientId());
            return new SimpleStringProperty(client != null ? client.getName() : "Unknown Client");
        });
    }

    /**
     * Loads all projects from the repository and populates the table.
     */
    private void loadProjects() {
        try {
            allProjects = projectRepository.findAll();
            projectsTableView.setItems(FXCollections.observableArrayList(allProjects));
            logger.info("Successfully loaded {} projects into the table.", allProjects.size());

            Set<ProjectStatus> uniqueStatuses = allProjects.stream()
                    .map(Project::getStatus)
                    .collect(Collectors.toSet());
            logger.info("Unique project statuses found are: {}", uniqueStatuses);
        } catch (DatabaseReadException e) {
            showStyledAlert(Alert.AlertType.ERROR, "Database Error", "Failed to Load Projects", "Could not retrieve project data from the database. Please check the logs.");
        }
    }

    /**
     * Handles the filter button action. Filters the project list based on
     * user input for name and status.
     */
    @FXML
    private void handleFilter() {
        List<Project> filteredList = allProjects.stream()
                .filter(p -> {
                    String nameFilter = nameFilterField.getText();
                    return nameFilter == null || nameFilter.isBlank() || p.getName().toLowerCase().contains(nameFilter.toLowerCase());
                })
                .filter(p -> {
                    ProjectStatus statusFilter = statusFilterComboBox.getValue();
                    return statusFilter == null || p.getStatus().equals(statusFilter);
                })
                .toList();
        projectsTableView.setItems(FXCollections.observableArrayList(filteredList));
    }

    /**
     * Clears all active filters and restores the full list of projects.
     */
    @FXML
    private void clearFilters() {
        nameFilterField.clear();
        statusFilterComboBox.getSelectionModel().clearSelection();
        projectsTableView.setItems(FXCollections.observableArrayList(allProjects));
    }

    /**
     * Opens the project edit dialog in "Add New" mode using the generic DialogHelper.
     */
    @FXML
    private void handleAddNewProject() {
        boolean saveClicked = DialogHelper.showEditDialog("project-edit-view.fxml", "Add New Project", null);
        if (saveClicked) {
            loadProjects();
        }
    }

    /**
     * Opens the project edit dialog in "Edit" mode for the selected project.
     * Shows a warning if no project is selected.
     */
    @FXML
    private void handleEditProject() {
        Project selectedProject = projectsTableView.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            boolean saveClicked = DialogHelper.showEditDialog("project-edit-view.fxml", "Edit Project", selectedProject);
            if (saveClicked) {
                loadProjects();
            }
        } else {
            showStyledAlert(Alert.AlertType.WARNING, "Selection Missing", "No Project Selected", "Please select a project from the table to edit.");
        }
    }

    /**
     * Deletes the selected project after user confirmation.
     * Shows a warning if no project is selected.
     */
    @FXML
    private void handleDeleteProject() {
        Project selectedProject = projectsTableView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            showStyledAlert(Alert.AlertType.WARNING, "Selection Missing", "No Project Selected", "Please select a project from the table to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this project?");
        confirmation.setContentText("Project: " + selectedProject.getName());
        styleAlert(confirmation);

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            projectRepository.deleteById(selectedProject.getId());
            logger.info("User confirmed deletion of project ID: {}", selectedProject.getId());
            loadProjects();
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
        dialogPane.getStylesheets().add(getClass().getResource(Config.CSS_STYLESHEET).toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}