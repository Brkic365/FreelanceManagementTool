package hr.tvz.java.freelance.freelancemanagementtool.util;

import hr.tvz.java.freelance.freelancemanagementtool.FreelanceManagementApplication;
import hr.tvz.java.freelance.freelancemanagementtool.controller.EditController;
import hr.tvz.java.freelance.freelancemanagementtool.model.Entity;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Generic class for displaying modals for editing/adding
 * Uses generics to make the code simpler
 */
public class DialogHelper {

    private static final Logger logger = LoggerFactory.getLogger(DialogHelper.class);

    /**
     * Opens modal dialog
     *
     * @param fxmlFile FXML file name
     * @param title Window title
     * @param entity Entity being edited
     * @param <T> Type of entity
     * @return True if user saved the changes, false if not
     */
    public static <T extends Entity> boolean showEditDialog(String fxmlFile, String title, T entity) {
        try {
            FXMLLoader loader = new FXMLLoader(FreelanceManagementApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(DialogHelper.class.getResource("/hr/tvz/java/freelance/freelancemanagementtool/css/styles.css").toExternalForm());

            EditController<T> controller = loader.getController();
            if (entity != null) {
                controller.setEntityToEdit(entity);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(FreelanceManagementApplication.getMainStage());
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

            return controller.isSaveClicked();

        } catch (IOException e) {
            logger.error("Failed to open the edit dialog: {}", fxmlFile, e);
            return false;
        }
    }
}