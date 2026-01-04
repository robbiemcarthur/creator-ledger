package org.creatorledger.income.application;

import org.creatorledger.income.domain.Income;
import org.creatorledger.income.api.IncomeId;
import org.creatorledger.common.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Application service for Income aggregate operations.
 * <p>
 * This service coordinates income-related use cases, delegating to the domain layer
 * for business logic and the repository for persistence.
 * </p>
 */
@Service
public class IncomeApplicationService {

    private final IncomeRepository incomeRepository;

    public IncomeApplicationService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    /**
     * Records new income in the system.
     *
     * @param command the record income command
     * @return the ID of the newly recorded income
     * @throws IllegalArgumentException if command is null
     */
    public IncomeId record(RecordIncomeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        Money amount = Money.of(new BigDecimal(command.amount()), command.currency());
        Income income = Income.record(
                IncomeId.generate(),
                command.userId(),
                command.eventId(),
                amount,
                command.description(),
                command.receivedDate()
        );

        incomeRepository.save(income);
        return income.id();
    }

    /**
     * Updates an existing income.
     *
     * @param command the update income command
     * @throws IllegalArgumentException if command is null
     * @throws IllegalStateException if income not found
     */
    public void update(UpdateIncomeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        Income existingIncome = incomeRepository.findById(command.incomeId())
                .orElseThrow(() -> new IllegalStateException("Income not found: " + command.incomeId()));

        Money amount = Money.of(new BigDecimal(command.amount()), command.currency());
        Income updatedIncome = existingIncome.update(
                amount,
                command.description(),
                command.receivedDate()
        );

        incomeRepository.save(updatedIncome);
    }

    /**
     * Marks an income as paid.
     *
     * @param incomeId the income ID
     * @throws IllegalArgumentException if incomeId is null
     * @throws IllegalStateException if income not found
     */
    public void markAsPaid(IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalStateException("Income not found: " + incomeId));

        Income paidIncome = income.markAsPaid();
        incomeRepository.save(paidIncome);
    }

    /**
     * Marks an income as overdue.
     *
     * @param incomeId the income ID
     * @throws IllegalArgumentException if incomeId is null
     * @throws IllegalStateException if income not found
     */
    public void markAsOverdue(IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalStateException("Income not found: " + incomeId));

        Income overdueIncome = income.markAsOverdue();
        incomeRepository.save(overdueIncome);
    }

    /**
     * Cancels an income.
     *
     * @param incomeId the income ID
     * @throws IllegalArgumentException if incomeId is null
     * @throws IllegalStateException if income not found
     */
    public void cancel(IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalStateException("Income not found: " + incomeId));

        Income cancelledIncome = income.cancel();
        incomeRepository.save(cancelledIncome);
    }

    /**
     * Finds an income by its ID.
     *
     * @param incomeId the income ID
     * @return an Optional containing the income if found, empty otherwise
     * @throws IllegalArgumentException if incomeId is null
     */
    public Optional<Income> findById(IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        return incomeRepository.findById(incomeId);
    }

    /**
     * Checks if an income exists with the given ID.
     *
     * @param incomeId the income ID
     * @return true if an income exists, false otherwise
     * @throws IllegalArgumentException if incomeId is null
     */
    public boolean existsById(IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        return incomeRepository.existsById(incomeId);
    }
}
