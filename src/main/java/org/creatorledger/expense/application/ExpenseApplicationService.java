package org.creatorledger.expense.application;

import org.creatorledger.common.Money;
import org.creatorledger.expense.api.ExpenseId;
import org.creatorledger.expense.domain.Expense;
import org.creatorledger.expense.domain.ExpenseRecorded;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ExpenseApplicationService {

    private final ExpenseRepository expenseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ExpenseApplicationService(final ExpenseRepository expenseRepository, final ApplicationEventPublisher eventPublisher) {
        if (expenseRepository == null) {
            throw new IllegalArgumentException("Expense repository cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("Event publisher cannot be null");
        }
        this.expenseRepository = expenseRepository;
        this.eventPublisher = eventPublisher;
    }

    public ExpenseId record(final RecordExpenseCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        final Money amount = Money.of(new BigDecimal(command.amount()), command.currency());
        final Expense expense = Expense.record(
                command.userId(),
                amount,
                command.category(),
                command.description(),
                command.incurredDate()
        );

        expenseRepository.save(expense);

        // Publish domain event
        final ExpenseRecorded event = ExpenseRecorded.of(
            expense.id(),
            expense.userId(),
            expense.amount(),
            expense.category(),
            expense.description(),
            expense.incurredDate()
        );
        eventPublisher.publishEvent(event);

        return expense.id();
    }

    public void update(final UpdateExpenseCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        final Expense existingExpense = expenseRepository.findById(command.expenseId())
                .orElseThrow(() -> new IllegalStateException("Expense not found: " + command.expenseId()));

        final Money amount = Money.of(new BigDecimal(command.amount()), command.currency());
        final Expense updatedExpense = existingExpense.update(
                amount,
                command.category(),
                command.description(),
                command.incurredDate()
        );

        expenseRepository.save(updatedExpense);
    }

    public Optional<Expense> findById(final ExpenseId expenseId) {
        if (expenseId == null) {
            throw new IllegalArgumentException("Expense ID cannot be null");
        }
        return expenseRepository.findById(expenseId);
    }

    public boolean existsById(final ExpenseId expenseId) {
        if (expenseId == null) {
            throw new IllegalArgumentException("Expense ID cannot be null");
        }
        return expenseRepository.existsById(expenseId);
    }
}
