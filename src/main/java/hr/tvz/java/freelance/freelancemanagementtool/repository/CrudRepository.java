package hr.tvz.java.freelance.freelancemanagementtool.repository;

import hr.tvz.java.freelance.freelancemanagementtool.exception.DatabaseReadException;
import hr.tvz.java.freelance.freelancemanagementtool.model.Entity;
import java.util.List;
import java.util.Optional;

/**
 * Defines the standard CRUD (Create, Read, Update, Delete) operations for a repository.
 *
 * @param <T> The entity type this repository manages, must extend Entity.
 * @param <ID> The type of the entity's ID.
 */
public interface CrudRepository<T extends Entity, ID> {

    /**
     * Saves a given entity.
     *
     * @param entity The entity to save.
     * @return The saved entity.
     */
    T save(T entity);

    /**
     * Retrieves all entities.
     *
     * @return A list of all entities.
     * @throws DatabaseReadException if an error occurs while reading from the database.
     */
    List<T> findAll() throws DatabaseReadException;

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return An Optional containing the entity if found, otherwise empty.
     * @throws DatabaseReadException if an error occurs while reading from the database.
     */
    Optional<T> findById(ID id) throws DatabaseReadException;

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete.
     */
    void deleteById(ID id);

    /**
     * Updates a given entity.
     *
     * @param entity The entity with updated information.
     * @return The updated entity.
     */
    T update(T entity);
}