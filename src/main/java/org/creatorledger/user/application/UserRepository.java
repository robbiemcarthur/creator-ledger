package org.creatorledger.user.application;

import org.creatorledger.user.domain.User;
import org.creatorledger.common.UserId;

import java.util.Optional;

/**
 * Repository interface for User aggregate persistence.
 * <p>
 * This is a port (in hexagonal architecture) that defines the contract
 * for persisting and retrieving User aggregates. The infrastructure layer
 * provides the actual implementation (adapter).
 * </p>
 */
public interface UserRepository {

    /**
     * Saves a user to the repository.
     * If the user already exists (based on ID), it will be updated.
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Finds a user by their unique identifier.
     *
     * @param id the user ID
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(UserId id);

    /**
     * Checks if a user exists with the given ID.
     *
     * @param id the user ID
     * @return true if a user exists, false otherwise
     */
    boolean existsById(UserId id);

    /**
     * Deletes a user from the repository.
     *
     * @param user the user to delete
     */
    void delete(User user);
}
