package hr.tvz.java.freelance.freelancemanagementtool.model;

import java.io.Serializable;

/**
 * A sealed base class for all data models in the application.
 * Ensures that only permitted classes can be entities.
 * Implements Serializable to allow objects to be written to streams.
 */
public sealed class Entity implements Serializable permits User, Client, Project, Task {
    private long id;

    /**
     * Constructor of Entity class
     *
     * @param id ID of entity
     */
    public Entity(long id) {
        this.id = id;
    }

    /**
     * Gets the ID
     *
     * @return ID of entity
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID
     *
     * @param id ID value
     */
    public void setId(long id) {
        this.id = id;
    }
}