package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;
import hr.tvz.java.freelance.freelancemanagementtool.model.AuditLog;
import hr.tvz.java.freelance.freelancemanagementtool.repository.AuditLogRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Audit Log screen. Displays change history from the serialized log file.
 */
public class AuditLogController {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);

    @FXML private TableView<AuditLog> auditLogTableView;
    @FXML private TableColumn<AuditLog, String> timestampColumn;
    @FXML private TableColumn<AuditLog, UserRole> roleColumn;
    @FXML private TableColumn<AuditLog, String> entityColumn;
    @FXML private TableColumn<AuditLog, String> oldValueColumn;
    @FXML private TableColumn<AuditLog, String> newValueColumn;

    private final AuditLogRepository auditLogRepository = new AuditLogRepository();

    /**
     * Initializes the controller. Sets up table columns and loads data from the audit log file.
     */
    @FXML
    public void initialize() {
        // Guideline #9: Setup columns to display all required change data
        timestampColumn.setCellValueFactory(cellData -> {
            LocalDateTime ldt = cellData.getValue().changedAt();
            String formatted = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return new SimpleStringProperty(formatted);
        });
        roleColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().userRole()));
        entityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().entityName()));
        oldValueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().oldValue()));
        newValueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().newValue()));

        loadAuditLogs();
        logger.info("Audit Log screen initialized.");
    }

    /**
     * Reads audit logs from the repository and populates the TableView.
     */
    private void loadAuditLogs() {
        // Guideline #6: Reading serialized data
        List<AuditLog> logs = auditLogRepository.readAll();
        auditLogTableView.setItems(FXCollections.observableArrayList(logs));
        logger.info("Loaded {} records from the audit log file.", logs.size());
    }
}