package hr.tvz.java.freelance.freelancemanagementtool.model;

import java.io.Serializable;

/**
 * A sealed base class for all data models in the application.
 * Ensures that only permitted classes can be entities.
 * Implements Serializable to allow objects to be written to streams.
 */
public sealed class Entity implements Serializable permits User, Client, Project, Task {
    private long id;

    public Entity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}