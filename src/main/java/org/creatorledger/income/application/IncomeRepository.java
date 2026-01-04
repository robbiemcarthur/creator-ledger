package org.creatorledger.income.application;

import org.creatorledger.income.domain.Income;
import org.creatorledger.income.api.IncomeId;

import java.util.Optional;

/**
 * Repository interface for Income aggregate persistence.
 * <p>
 * This is a port (in hexagonal architecture) that defines the contract
 * for persisting and retrieving Income aggregates. The infrastructure layer
 * provides the actual implementation (adapter).
 * </p>
 */
public interface IncomeRepository {

    /**
     * Saves an income to the repository.
     * If the income already exists (based on ID), it will be updated.
     *
     * @param income the income to save
     * @return the saved income
     */
    Income save(Income income);

    /**
     * Finds an income by its unique identifier.
     *
     * @param id the income ID
     * @return an Optional containing the income if found, empty otherwise
     */
    Optional<Income> findById(IncomeId id);

    /**
     * Checks if an income exists with the given ID.
     *
     * @param id the income ID
     * @return true if an income exists, false otherwise
     */
    boolean existsById(IncomeId id);

    /**
     * Deletes an income from the repository.
     *
     * @param income the income to delete
     */
    void delete(Income income);
}
