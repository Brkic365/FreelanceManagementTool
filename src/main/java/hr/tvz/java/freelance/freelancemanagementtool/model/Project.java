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
    private long assignedUserId;
    private LocalDate startDate;
    private LocalDate deadline;
    private BigDecimal budget;
    private ProjectStatus status;

    /**
     * Project class constructor
     *
     * @param builder The builder instance containing all the data.
     */
    private Project(Builder builder) {
        super(builder.id);
        this.name = builder.name;
        this.description = builder.description;
        this.clientId = builder.clientId;
        this.assignedUserId = builder.assignedUserId;
        this.startDate = builder.startDate;
        this.deadline = builder.deadline;
        this.budget = builder.budget;
        this.status = builder.status;
    }

    /**
     * Gets name of the project
     *
     * @return Name string
     */
    public String getName() { return name; }

    /**
     * Sets name of the project
     *
     * @param name Name string
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets description of the project
     *
     * @return Description string
     */
    public String getDescription() { return description; }

    /**
     * Sets description of the project
     *
     * @param description Description string
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets Client ID of the project
     *
     * @return Client ID number
     */
    public long getClientId() { return clientId; }

    /**
     * Sets Client ID of the project
     *
     * @param clientId Client ID number
     */
    public void setClientId(long clientId) { this.clientId = clientId; }

    /**
     * Gets ID of the assigned user of the project
     *
     * @return ID of the assigned user number
     */
    public long getAssignedUserId() { return assignedUserId; }

    /**
     * Sets ID of the assigned user of the project
     *
     * @param assignedUserId ID of the assigned user number
     */
    public void setAssignedUserId(long assignedUserId) { this.assignedUserId = assignedUserId; }

    /**
     * Gets start date of the project
     *
     * @return Start Date value
     */
    public LocalDate getStartDate() { return startDate; }

    /**
     * Sets start date of the project
     *
     * @param startDate Start Date value
     */
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /**
     * Gets deadline of the project
     *
     * @return Deadline value
     */
    public LocalDate getDeadline() { return deadline; }

    /**
     * Sets deadline of the project
     *
     * @param deadline Deadline value
     */
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    /**
     * Gets budget of the project
     *
     * @return Budget value
     */
    public BigDecimal getBudget() { return budget; }

    /**
     * Sets budget of the project
     *
     * @param budget Budget value
     */
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    /**
     * Gets status of the project
     *
     * @return Project status value
     */
    public ProjectStatus getStatus() { return status; }

    /**
     * Sets status of the project
     *
     * @param status Project status value
     */
    public void setStatus(ProjectStatus status) { this.status = status; }

    /**
     * Overrides the original toString function so it returns a string with projects id, name and status
     *
     * @return A string with projects id, name and status
     */
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
        // Builder fields remain the same
        private long id;
        private String name;
        private String description;
        private long clientId;
        private long assignedUserId;
        private LocalDate startDate;
        private LocalDate deadline;
        private BigDecimal budget;
        private ProjectStatus status;

        /**
         * Builder constructor
         *
         * @param id ID of the project
         */
        public Builder(long id) { this.id = id; }

        /**
         * Builder name setter
         *
         * @param name Name of the project
         */
        public Builder withName(String name) { this.name = name; return this; }

        /**
         * Builder description setter
         *
         * @param description Description of the project
         */
        public Builder withDescription(String description) { this.description = description; return this; }

        /**
         * Builder client ID setter
         *
         * @param clientId Client ID of the project
         */
        public Builder withClientId(long clientId) { this.clientId = clientId; return this; }

        /**
         * Builder assigned user ID setter
         *
         * @param userId Assigned user ID
         */
        public Builder withAssignedUserId(long userId) { this.assignedUserId = userId; return this; }

        /**
         * Builder start date setter
         *
         * @param date Start date of the project
         */
        public Builder withStartDate(LocalDate date) { this.startDate = date; return this; }

        /**
         * Builder deadline setter
         *
         * @param date Deadline of the project
         */
        public Builder withDeadline(LocalDate date) { this.deadline = date; return this; }

        /**
         * Builder budget setter
         *
         * @param budget Budget of the project
         */
        public Builder withBudget(BigDecimal budget) { this.budget = budget; return this; }

        /**
         * Builder status setter
         *
         * @param status Project status
         */
        public Builder withStatus(ProjectStatus status) { this.status = status; return this; }

        /**
         * Builds and returns a new Project object from the builder's state.
         *
         * @return A new, configured Project instance.
         */
        public Project build() {
            return new Project(this);
        }
    }
}