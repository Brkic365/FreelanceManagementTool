package hr.tvz.java.freelance.freelancemanagementtool.repository;

import hr.tvz.java.freelance.freelancemanagementtool.exception.DataSerializationException;
import hr.tvz.java.freelance.freelancemanagementtool.model.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages the storage and retrieval of audit logs using binary serialization.
 * All write operations are performed asynchronously on a separate thread.
 */
public class AuditLogRepository {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogRepository.class);
    private static final String AUDIT_LOG_FILE = "data/audit_log.dat";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Saves a new audit log entry asynchronously.
     * The actual file writing is submitted to a single-thread executor
     * to avoid blocking the main application thread.
     *
     * @param log The AuditLog record to save.
     */
    public void save(AuditLog log) {
        executor.submit(() -> writeLogToFile(log));
    }

    /**
     * The core private method that writes a log to the binary file.
     * This method is synchronized to prevent race conditions if it were
     * ever called from multiple threads directly.
     *
     * @param newLog The AuditLog record to write.
     */
    private synchronized void writeLogToFile(AuditLog newLog) {
        logger.debug("Writing new audit log to file: {}", newLog);
        List<AuditLog> logs = readAll();
        logs.add(newLog);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUDIT_LOG_FILE))) {
            oos.writeObject(logs);
            logger.info("Successfully wrote audit log entry for entity: {}", newLog.entityName());
        } catch (IOException e) {
            throw new DataSerializationException("Failed to write audit log.", e);
        }
    }

    /**
     * Reads all audit log entries from the binary file.
     * This method is synchronized to ensure it reads a consistent state of the file.
     *
     * @return A list of all AuditLog records. Returns an empty list if the file doesn't exist or is empty.
     */
    public synchronized List<AuditLog> readAll() {
        File file = new File(AUDIT_LOG_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                List<AuditLog> logs = (List<AuditLog>) obj;
                return logs;
            }
        } catch (FileNotFoundException e) {
            logger.info("Audit log file not found, will be created on first write. This is normal.", e);
        } catch (EOFException e) {
            logger.warn("Audit log file is empty or corrupted, starting fresh.", e);
        } catch (IOException | ClassNotFoundException e) {
            throw new DataSerializationException("Failed to read audit log.", e);
        }

        return new ArrayList<>();
    }


    /**
     * Shuts down the executor service.
     * This should be called when the application is closing to ensure
     * all pending log writes are completed.
     */
    public static void shutdown() {
        logger.info("Shutting down AuditLogRepository executor service.");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in the specified time.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Audit log shutdown was interrupted.", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}