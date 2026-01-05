package org.creatorledger.reporting.domain;

import org.creatorledger.common.Money;
import org.creatorledger.expense.api.ExpenseCategory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public record CategoryTotals(Map<ExpenseCategory, Money> totals) {

    public static CategoryTotals empty() {
        return new CategoryTotals(Collections.emptyMap());
    }

    public static CategoryTotals of(Map<ExpenseCategory, Money> totals) {
        if (totals == null) {
            throw new IllegalArgumentException("Category totals map cannot be null");
        }
        return new CategoryTotals(Map.copyOf(totals));
    }

    public Money totalFor(ExpenseCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return totals.getOrDefault(category, Money.gbp("0.00"));
    }

    public Money overallTotal() {
        return totals.values().stream()
                .reduce(Money.gbp("0.00"), Money::add);
    }

    public Set<ExpenseCategory> categories() {
        return totals.keySet();
    }


    public boolean isEmpty() {
        return totals.isEmpty();
    }

    @Override
    public String toString() {
        if (totals.isEmpty()) {
            return "CategoryTotals[empty]";
        }
        return "CategoryTotals%s".formatted(totals);
    }
}
