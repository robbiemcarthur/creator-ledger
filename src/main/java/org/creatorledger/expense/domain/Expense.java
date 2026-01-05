package org.creatorledger.expense.domain;

import org.creatorledger.expense.api.ExpenseId;
import org.creatorledger.common.Money;
import org.creatorledger.user.api.UserId;
import org.creatorledger.expense.api.ExpenseCategory;

import java.time.LocalDate;

public record Expense(
    ExpenseId id,
    UserId userId,
    Money amount,
    ExpenseCategory category,
    String description,
    LocalDate incurredDate
) {

    public static Expense record(final UserId userId, final Money amount, final ExpenseCategory category, final String description, final LocalDate incurredDate) {
        return record(ExpenseId.generate(), userId, amount, category, description, incurredDate);
    }

    public static Expense record(final ExpenseId id, final UserId userId, final Money amount, final ExpenseCategory category, final String description, final LocalDate incurredDate) {
        if (id == null) throw new IllegalArgumentException("ExpenseId cannot be null");
        if (userId == null) throw new IllegalArgumentException("UserId cannot be null");
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description cannot be null or blank");
        if (incurredDate == null) throw new IllegalArgumentException("IncurredDate cannot be null");

        return new Expense(id, userId, amount, category, description.trim(), incurredDate);
    }

    public Expense update(final Money amount, final ExpenseCategory category, final String description, final LocalDate incurredDate) {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description cannot be null or blank");
        if (incurredDate == null) throw new IllegalArgumentException("IncurredDate cannot be null");

        return new Expense(this.id, this.userId, amount, category, description.trim(), incurredDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Expense other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Expense[id=" + id + ", amount=" + amount + ", category=" + category +
               ", description=" + description + ", incurredDate=" + incurredDate + "]";
    }
}
