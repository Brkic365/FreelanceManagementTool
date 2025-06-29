// File: model/Task.java
package hr.tvz.java.freelance.freelancemanagementtool.model;

/**
 * Represents a single task within a project.
 */
public final class Task extends Entity {
    private String title;
    private String description;
    private long projectId;
    private boolean isCompleted;

    public Task(long id, String title, String description, long projectId, boolean isCompleted) {
        super(id);
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.isCompleted = isCompleted;
    }
    // Add Getters and Setters...
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}