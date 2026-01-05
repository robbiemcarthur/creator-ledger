package org.creatorledger.expense.infrastructure.web;

import org.creatorledger.expense.domain.Expense;

public record ExpenseResponse(
        String id,
        String userId,
        String amount,
        String currency,
        String category,
        String description,
        String incurredDate
) {

    public static ExpenseResponse from(final Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }
        return new ExpenseResponse(
                expense.id().value().toString(),
                expense.userId().value().toString(),
                expense.amount().amount().toString(),
                expense.amount().currency(),
                expense.category().name(),
                expense.description(),
                expense.incurredDate().toString()
        );
    }
}
