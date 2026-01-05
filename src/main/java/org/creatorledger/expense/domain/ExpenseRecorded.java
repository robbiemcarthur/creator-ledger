package org.creatorledger.expense.domain;

import org.creatorledger.common.Money;
import org.creatorledger.expense.api.ExpenseId;
import org.creatorledger.user.api.UserId;
import org.creatorledger.expense.api.ExpenseCategory;

import java.time.Instant;
import java.time.LocalDate;

public record ExpenseRecorded(
    ExpenseId expenseId,
    UserId userId,
    Money amount,
    ExpenseCategory category,
    String description,
    LocalDate incurredDate,
    Instant occurredAt
) {

    public static ExpenseRecorded of(final ExpenseId expenseId, final UserId userId, final Money amount, final ExpenseCategory category, final String description, final LocalDate incurredDate) {
        return new ExpenseRecorded(expenseId, userId, amount, category, description, incurredDate, Instant.now());
    }

    @Override
    public String toString() {
        return "ExpenseRecorded[expenseId=" + expenseId + ", userId=" + userId +
               ", amount=" + amount + ", category=" + category + ", description=" + description +
               ", incurredDate=" + incurredDate + ", occurredAt=" + occurredAt + "]";
    }
}
