package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.FreelanceManagementApplication;
import hr.tvz.java.freelance.freelancemanagementtool.enums.ProjectStatus;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Client;
import hr.tvz.java.freelance.freelancemanagementtool.model.Project;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ClientDatabaseRepository;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ProjectDatabaseRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller for the Project Search screen. Handles displaying, filtering,
 * and managing project data.
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
     * Initializes the controller. Sets up table columns and loads initial data.
     */
    @FXML
    public void initialize() {
        loadClientsIntoMap(); // Load clients first
        setupTableColumns();
        statusFilterComboBox.setItems(FXCollections.observableArrayList(ProjectStatus.values()));
        loadProjects();
    }

    /**
     * Loads all clients into a Map for efficient lookup. Guideline #4 (Maps).
     */
    private void loadClientsIntoMap() {
        try {
            clientMap = clientRepository.findAll().stream()
                    .collect(Collectors.toMap(Client::getId, Function.identity()));
        } catch (DatabaseReadException e) {
            logger.error("CRITICAL: Failed to load clients for project view.", e);
            clientMap = Map.of(); // Use an empty map to prevent NullPointerExceptions
        }
    }

    /**
     * Configures the cell value factories for the TableView columns.
     */
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        deadlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        budgetColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBudget().toString()));

        // FIX: Display client name instead of ID using the map
        clientColumn.setCellValueFactory(cellData -> {
            Client client = clientMap.get(cellData.getValue().getClientId());
            return new SimpleStringProperty(client != null ? client.getName() : "Unknown Client");
        });
    }

    /**
     * Loads all projects from the database and populates the table.
     */
    private void loadProjects() {
        try {
            allProjects = projectRepository.findAll();
            projectsTableView.setItems(FXCollections.observableArrayList(allProjects));
            logger.info("Successfully loaded {} projects into the table.", allProjects.size());
        } catch (DatabaseReadException e) {
            logger.error("Failed to load projects from the database.", e);
            new Alert(Alert.AlertType.ERROR, "Error loading data from database. Please check logs.").showAndWait();
        }
    }

    /**
     * Handles the filter button action.
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
                .collect(Collectors.toList());

        projectsTableView.setItems(FXCollections.observableArrayList(filteredList));
        logger.info("Applied filters. Displaying {} projects.", filteredList.size());
    }

    /**
     * Clears all active filters.
     */
    @FXML
    private void clearFilters() {
        nameFilterField.clear();
        statusFilterComboBox.getSelectionModel().clearSelection();
        projectsTableView.setItems(FXCollections.observableArrayList(allProjects));
        logger.info("Filters cleared.");
    }

    /**
     * Opens the project edit window in "Add New" mode.
     */
    @FXML
    private void handleAddNewProject() {
        showProjectEditDialog(null);
    }

    /**
     * Opens the project edit window in "Edit" mode for the selected project.
     */
    @FXML
    private void handleEditProject() {
        Project selectedProject = projectsTableView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a project to edit.").show();
            return;
        }
        showProjectEditDialog(selectedProject);
    }

// Inside ProjectSearchController.java

// Replace the existing handleDeleteProject method with this one
    /**
     * Deletes the selected project after user confirmation.
     */
    @FXML
    private void handleDeleteProject() {
        Project selectedProject = projectsTableView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a project to delete.").show();
            return;
        }

        // Guideline #8: Confirmation Dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this project?");
        confirmation.setContentText("Project: " + selectedProject.getName());

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            projectRepository.deleteById(selectedProject.getId());
            logger.info("User confirmed to delete project ID: {}", selectedProject.getId());
            // Refresh the table view to show the project has been removed
            loadProjects();
        }
    }

    /**
     * A helper method to show the project edit/add dialog.
     * @param project The project to edit, or null to add a new one.
     */
    private void showProjectEditDialog(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(FreelanceManagementApplication.class.getResource("project-edit-view.fxml"));
            Scene scene = new Scene(loader.load());

            ProjectEditController controller = loader.getController();
            if (project != null) {
                controller.setProjectToEdit(project);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(project == null ? "Add New Project" : "Edit Project");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FreelanceManagementApplication.getMainStage());
            dialogStage.setScene(scene);

            dialogStage.showAndWait(); // Show window and wait for it to be closed

            // After the dialog is closed, refresh the data in the table
            loadProjects();

        } catch (IOException e) {
            logger.error("Failed to open the project edit dialog.", e);
        }
    }
}