package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.enums.ProjectStatus;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Client;
import hr.tvz.java.freelance.freelancemanagementtool.model.Project;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ClientDatabaseRepository;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ProjectDatabaseRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for the project add/edit form.
 */
public class ProjectEditController implements EditController<Project> {

    private static final Logger logger = LoggerFactory.getLogger(ProjectEditController.class);

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField budgetField;
    @FXML private ComboBox<ProjectStatus> statusComboBox;

    private final ClientDatabaseRepository clientRepository = new ClientDatabaseRepository();
    private final ProjectDatabaseRepository projectRepository = new ProjectDatabaseRepository();

    private Project projectToEdit;

    private boolean isSaveClicked = false;

    /**
     * Initializes the controller, populating the choice boxes.
     */
    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(ProjectStatus.values()));
        loadClients();
    }

    /**
     * Loads clients from the database and populates the client ComboBox.
     */
    private void loadClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            clientComboBox.setItems(FXCollections.observableArrayList(clients));
        } catch (DatabaseReadException e) {
            logger.error("Failed to load clients for the form.", e);
            new Alert(Alert.AlertType.ERROR, "Could not load client data.").showAndWait();
        }
    }

    /**
     * Pre-populates the form fields with data from an existing project for editing.
     * @param entity The project to be edited.
     */
    @Override
    public void setEntityToEdit(Project entity) {
        this.projectToEdit = entity;
        nameField.setText(entity.getName());
        descriptionArea.setText(entity.getDescription());
        startDatePicker.setValue(entity.getStartDate());
        deadlinePicker.setValue(entity.getDeadline());
        budgetField.setText(entity.getBudget().toPlainString());
        statusComboBox.setValue(entity.getStatus());

        clientComboBox.getItems().stream()
                .filter(c -> c.getId() == entity.getClientId())
                .findFirst()
                .ifPresent(clientComboBox::setValue);
    }

    /**
     * Handles the save button action. Validates input, confirms with the user,
     * and saves the project to the database.
     */
    @FXML
    private void handleSave() {
        if (nameField.getText().isBlank() || clientComboBox.getValue() == null || statusComboBox.getValue() == null || budgetField.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields (Name, Client, Status, Budget).").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Save");
        confirmation.setHeaderText("Are you sure you want to save these changes?");
        confirmation.setContentText("Project: " + nameField.getText());

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (projectToEdit == null) {
                    Project newProject = new Project.Builder(0)
                            .withName(nameField.getText())
                            .withDescription(descriptionArea.getText())
                            .withClientId(clientComboBox.getValue().getId())
                            .withStartDate(startDatePicker.getValue())
                            .withDeadline(deadlinePicker.getValue())
                            .withBudget(new BigDecimal(budgetField.getText()))
                            .withStatus(statusComboBox.getValue())
                            .build();
                    projectRepository.save(newProject);
                    logger.info("User confirmed to save new project: {}", newProject.getName());
                } else {
                    projectToEdit.setName(nameField.getText());
                    projectToEdit.setDescription(descriptionArea.getText());
                    projectToEdit.setClientId(clientComboBox.getValue().getId());
                    projectToEdit.setStartDate(startDatePicker.getValue());
                    projectToEdit.setDeadline(deadlinePicker.getValue());
                    projectToEdit.setBudget(new BigDecimal(budgetField.getText()));
                    projectToEdit.setStatus(statusComboBox.getValue());
                    projectRepository.update(projectToEdit);
                    logger.info("User confirmed to update project: {}", projectToEdit.getName());
                }
                isSaveClicked = true;
                closeWindow();
            } catch (NumberFormatException e) {
                logger.error("Invalid budget format entered.", e);
                new Alert(Alert.AlertType.ERROR, "Invalid format for Budget. Please enter a valid number.").show();
            }
        }
    }


    /**
     * Cancels the edit.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Closes the form window.
     */
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    /**
     * A method to check if the save button was clicked, used by the calling controller.
     *
     * @return true if save was clicked, false otherwise.
     */
    @Override
    public boolean isSaveClicked() {
        return isSaveClicked;
    }
}