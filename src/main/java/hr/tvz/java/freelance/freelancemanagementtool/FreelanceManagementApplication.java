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

    private static final String CSS_PATH = "/hr/tvz/java/freelance/freelancemanagementtool/css/styles.css";

    /**
     * Entry point for setting up the primary stage
     */
    @Override
    @SuppressWarnings("java:S2696")
    public void start(Stage stage) throws IOException {
        mainStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(FreelanceManagementApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        mainStage.setTitle("Login - Freelance Tool");
        mainStage.setScene(scene);
        mainStage.show();
        logger.info("Application started, showing login screen.");
        new DeadlineReminderThread().start();
        mainStage.setOnCloseRequest(event -> {
            AuditLogRepository.shutdown();
            logger.info("Application closing, shutdown hooks initiated.");
        });
    }

    /**
     * Main function
     *
     * @param args Props
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Gets the main stage object
     *
     * @return Main stage object
     */
    public static Stage getMainStage() {
        return mainStage;
    }
}