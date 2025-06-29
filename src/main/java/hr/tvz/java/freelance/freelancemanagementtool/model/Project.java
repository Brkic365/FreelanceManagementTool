package hr.tvz.java.freelance.freelancemanagementtool.model;

import hr.tvz.java.freelance.freelancemanagementtool.enums.ProjectStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a project managed by a freelancer.
 * Extends the base Entity class and uses the Builder pattern for construction.
 */
public final class Project extends Entity {
    private String name;
    private String description;
    private long clientId;
    private long assignedUserId; // The freelancer
    private LocalDate startDate;
    private LocalDate deadline;
    private BigDecimal budget;
    private ProjectStatus status;

    /**
     * Private constructor to be used exclusively by the Builder.
     *
     * @param id The unique ID of the project.
     * @param name The name of the project.
     * @param description A detailed description of the project.
     * @param clientId The ID of the client this project belongs to.
     * @param assignedUserId The ID of the user (freelancer) assigned to this project.
     * @param startDate The date the project started.
     * @param deadline The project's deadline.
     * @param budget The allocated budget for the project.
     * @param status The current status of the project.
     */
    private Project(long id, String name, String description, long clientId, long assignedUserId, LocalDate startDate, LocalDate deadline, BigDecimal budget, ProjectStatus status) {
        super(id);
        this.name = name;
        this.description = description;
        this.clientId = clientId;
        this.assignedUserId = assignedUserId;
        this.startDate = startDate;
        this.deadline = deadline;
        this.budget = budget;
        this.status = status;
    }

    // --- Getters and Setters ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getClientId() { return clientId; }
    public void setClientId(long clientId) { this.clientId = clientId; }

    public long getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(long assignedUserId) { this.assignedUserId = assignedUserId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Builder pattern for creating Project objects.
     */
    public static class Builder {
        private long id;
        private String name;
        private String description;
        private long clientId;
        private long assignedUserId;
        private LocalDate startDate;
        private LocalDate deadline;
        private BigDecimal budget;
        private ProjectStatus status;

        public Builder(long id) { this.id = id; }
        public Builder withName(String name) { this.name = name; return this; }
        public Builder withDescription(String description) { this.description = description; return this; }
        public Builder withClientId(long clientId) { this.clientId = clientId; return this; }
        public Builder withAssignedUserId(long userId) { this.assignedUserId = userId; return this; }
        public Builder withStartDate(LocalDate date) { this.startDate = date; return this; }
        public Builder withDeadline(LocalDate date) { this.deadline = date; return this; }
        public Builder withBudget(BigDecimal budget) { this.budget = budget; return this; }
        public Builder withStatus(ProjectStatus status) { this.status = status; return this; }

        /**
         * Builds and returns a new Project object from the builder's state.
         * @return A new, configured Project instance.
         */
        public Project build() {
            return new Project(id, name, description, clientId, assignedUserId, startDate, deadline, budget, status);
        }
    }
}