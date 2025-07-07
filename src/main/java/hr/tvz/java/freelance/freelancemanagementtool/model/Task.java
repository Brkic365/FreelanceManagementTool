package hr.tvz.java.freelance.freelancemanagementtool.model;

/**
 * Represents a single task within a project.
 */
public final class Task extends Entity {
    private String title;
    private boolean isCompleted;

    public Task(long id, String title, boolean isCompleted) {
        super(id);
        this.title = title;
        this.isCompleted = isCompleted;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}