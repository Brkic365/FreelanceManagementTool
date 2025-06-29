package hr.tvz.java.freelance.freelancemanagementtool;

import hr.tvz.java.freelance.freelancemanagementtool.repository.AuditLogRepository;
import hr.tvz.java.freelance.freelancemanagementtool.thread.DeadlineReminderThread;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The main entry point for the Freelance Management Tool application.
 */
public class FreelanceManagementApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(FreelanceManagementApplication.class);
    private static Stage mainStage;

    /**
     * Starts the JavaFX application, setting up the initial stage and showing the login screen.
     *
     * @param stage The primary stage for this application.
     * @throws IOException if the login FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(FreelanceManagementApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Login - Freelance Tool");
        stage.setScene(scene);
        stage.show();
        logger.info("Application started, showing login screen.");

        // Guideline #11: Start the background thread
        new DeadlineReminderThread().start();

        stage.setOnCloseRequest(event -> {
            AuditLogRepository.shutdown();
            logger.info("Application closing, shutdown hooks initiated.");
        });
    }

    /**
     * The main method, which launches the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Returns the primary stage of the application.
     *
     * @return The main Stage object.
     */
    public static Stage getMainStage() {
        return mainStage;
    }
}