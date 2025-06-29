package hr.tvz.java.freelance.freelancemanagementtool.thread;

import hr.tvz.java.freelance.freelancemanagementtool.enums.ProjectStatus;
import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Project;
import hr.tvz.java.freelance.freelancemanagementtool.repository.ProjectDatabaseRepository;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * A background thread that periodically checks for projects with upcoming deadlines.
 */
public class DeadlineReminderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(DeadlineReminderThread.class);
    private static final long SLEEP_INTERVAL_MS = 60000; // Check every 60 seconds
    private static final int DEADLINE_THRESHOLD_DAYS = 7;

    private final ProjectDatabaseRepository projectRepository;

    public DeadlineReminderThread() {
        this.projectRepository = new ProjectDatabaseRepository();
        setDaemon(true); // Ensures this thread doesn't prevent the app from exiting
    }

    /**
     * The main execution logic for the thread.
     */
    @Override
    public void run() {
        logger.info("DeadlineReminderThread started.");
        while (!isInterrupted()) {
            try {
                checkForUpcomingDeadlines();
                Thread.sleep(SLEEP_INTERVAL_MS);
            } catch (InterruptedException e) {
                logger.warn("DeadlineReminderThread was interrupted and will now exit.");
                Thread.currentThread().interrupt(); // Preserve the interrupted status
            } catch (DatabaseReadException e) {
                logger.error("Error reading projects from database in reminder thread.", e);
            }
        }
    }

    /**
     * Fetches projects and checks for deadlines within the threshold.
     * If any are found, it displays an alert on the JavaFX Application Thread.
     */
    private void checkForUpcomingDeadlines() throws DatabaseReadException {
        logger.debug("Checking for upcoming deadlines...");
        List<Project> activeProjects = projectRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS)
                .toList();

        for (Project project : activeProjects) {
            long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline());
            if (daysUntilDeadline > 0 && daysUntilDeadline <= DEADLINE_THRESHOLD_DAYS) {
                String message = String.format("Project '%s' is due in %d day(s)!", project.getName(), daysUntilDeadline);
                logger.info("Deadline alert: {}", message);

                // Guideline #11: Updating the UI from a background thread must use Platform.runLater
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Deadline Reminder");
                    alert.setHeaderText("Upcoming Project Deadline");
                    alert.setContentText(message);
                    alert.showAndWait();
                });
            }
        }
    }
}