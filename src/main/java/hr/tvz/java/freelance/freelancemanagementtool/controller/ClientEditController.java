package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.model.Client;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ClientDatabaseRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the client add/edit form.
 */
public class ClientEditController implements EditController<Client> {

    private static final Logger logger = LoggerFactory.getLogger(ClientEditController.class);

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField contactPersonField;

    private final ClientDatabaseRepository clientRepository = new ClientDatabaseRepository();
    private Client clientToEdit;
    private boolean isSaveClicked = false;

    /**
     * Pre-populates the form fields with data from an existing client for editing.
     * @param client The client to be edited.
     */
    @Override
    public void setEntityToEdit(Client client) {
        this.clientToEdit = client;
        nameField.setText(client.getName());
        emailField.setText(client.getEmail());
        contactPersonField.setText(client.getContactPerson());
    }

    /**
     * A method to check if the save button was clicked, used by the calling controller.
     * @return true if save was clicked, false otherwise.
     */
    public boolean isSaveClicked() {
        return isSaveClicked;
    }

    /**
     * Handles the save button action. Validates input, confirms with the user,
     * and saves the client to the database.
     */
    @FXML
    private void handleSave() {
        if (!isInputValid()) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to save this client?", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm Save");
        confirmation.setHeaderText("Save Client: " + nameField.getText());

        if (confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            if (clientToEdit == null) {
                Client newClient = new Client(0, nameField.getText(), emailField.getText(), contactPersonField.getText());
                clientRepository.save(newClient);
                logger.info("User confirmed saving new client: {}", newClient.getName());
            } else {
                clientToEdit.setName(nameField.getText());
                clientToEdit.setEmail(emailField.getText());
                clientToEdit.setContactPerson(contactPersonField.getText());
                clientRepository.update(clientToEdit);
                logger.info("User confirmed updating client: {}", clientToEdit.getName());
            }
            isSaveClicked = true;
            closeWindow();
        }
    }

    /**
     * Handles the cancel button action.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid.
     */
    private boolean isInputValid() {
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            errorMessage += "No valid name!\n";
        }
        if (emailField.getText() == null || emailField.getText().isBlank() || !emailField.getText().contains("@")) {
            errorMessage += "No valid email!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Closes the form window.
     */
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}