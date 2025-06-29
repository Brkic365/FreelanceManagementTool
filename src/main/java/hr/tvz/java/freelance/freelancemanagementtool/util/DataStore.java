// File: util/DataStore.java
package hr.tvz.java.freelance.freelancemanagementtool.util;

import hr.tvz.java.freelance.freelancemanagementtool.model.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic class that acts as a simple in-memory store for any type of Entity.
 * @param T The type of entity this store will hold, must extend Entity.
 */
public class DataStore<T extends Entity> {
    private List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(item);
    }

    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    public void clear() {
        items.clear();
    }
}