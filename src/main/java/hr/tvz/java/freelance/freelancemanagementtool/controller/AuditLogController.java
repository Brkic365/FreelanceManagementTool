package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.enums.UserRole;
import hr.tvz.java.freelance.freelancemanagementtool.model.AuditLog;
import hr.tvz.java.freelance.freelancemanagementtool.repository.AuditLogRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Audit Log screen. Displays change history from the serialized log file.
 * Allows users to double-click a log entry to view full details.
 */
public class AuditLogController {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML private TableView<AuditLog> auditLogTableView;
    @FXML private TableColumn<AuditLog, String> timestampColumn;
    @FXML private TableColumn<AuditLog, UserRole> roleColumn;
    @FXML private TableColumn<AuditLog, String> entityColumn;
    @FXML private TableColumn<AuditLog, String> oldValueColumn;
    @FXML private TableColumn<AuditLog, String> newValueColumn;

    private final AuditLogRepository auditLogRepository = new AuditLogRepository();

    /**
     * Initializes the Audit Log Controller
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableClickListener();
        loadAuditLogs();
        logger.info("Audit Log screen initialized.");
    }

    /**
     * Connects the table columns with variables
     */
    private void setupTableColumns() {
        timestampColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().changedAt().format(FORMATTER)));
        roleColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().userRole()));
        entityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().entityName()));
        oldValueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().oldValue()));
        newValueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().newValue()));
    }

    /**
     * Listens for a double click on the log
     */
    private void setupTableClickListener() {
        auditLogTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                AuditLog selectedLog = auditLogTableView.getSelectionModel().getSelectedItem();
                if (selectedLog != null) {
                    showLogDetails(selectedLog);
                }
            }
        });
    }

    /**
     * Loads all the logs from the repository
     */
    private void loadAuditLogs() {
        List<AuditLog> logs = auditLogRepository.readAll();
        auditLogTableView.setItems(FXCollections.observableArrayList(logs));
        logger.info("Loaded {} records from the audit log file.", logs.size());
    }

    /**
     * Displays a dialog window with the full details of a given audit log entry.
     * The layout is a structured GridPane.
     *
     * @param log The AuditLog object to display.
     */
    private void showLogDetails(AuditLog log) {
        logger.info("Showing details for audit log of entity: {}", log.entityName());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Audit Log Details");
        alert.setHeaderText("Full details for change on '" + log.entityName() + "'");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/hr/tvz/java/freelance/freelancemanagementtool/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        dialogPane.setContent(createDetailsPane(log));
        alert.setResizable(true);

        alert.showAndWait();
    }

    /**
     * Creates a GridPane containing the structured details of the audit log.
     *
     * @param log The AuditLog to display.
     * @return A configured GridPane ready to be set as dialog content.
     */
    private GridPane createDetailsPane(AuditLog log) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        Label timestampLabel = new Label("Timestamp:");
        TextField timestampField = new TextField(log.changedAt().format(FORMATTER));
        timestampField.setEditable(false);

        Label roleLabel = new Label("User Role:");
        TextField roleField = new TextField(log.userRole().toString());
        roleField.setEditable(false);

        Label oldLabel = new Label("Old Value:");
        TextArea oldArea = new TextArea(log.oldValue());
        oldArea.setEditable(false);
        oldArea.setWrapText(true);
        oldArea.setPrefRowCount(5);

        Label newLabel = new Label("New Value:");
        TextArea newArea = new TextArea(log.newValue());
        newArea.setEditable(false);
        newArea.setWrapText(true);
        newArea.setPrefRowCount(5);

        grid.add(timestampLabel, 0, 0);
        grid.add(timestampField, 1, 0);
        grid.add(roleLabel, 0, 1);
        grid.add(roleField, 1, 1);
        grid.add(oldLabel, 0, 2);
        grid.add(oldArea, 0, 3, 2, 1);
        grid.add(newLabel, 0, 4);
        grid.add(newArea, 0, 5, 2, 1);

        GridPane.setVgrow(oldArea, Priority.ALWAYS);
        GridPane.setVgrow(newArea, Priority.ALWAYS);

        return grid;
    }
}