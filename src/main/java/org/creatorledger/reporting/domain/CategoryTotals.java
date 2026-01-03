package org.creatorledger.reporting.domain;

import org.creatorledger.common.Money;
import org.creatorledger.expense.api.ExpenseCategory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Value object representing the total expenses grouped by category.
 * <p>
 * This is used to provide a breakdown of expenses within a tax year summary,
 * helping users understand their spending patterns across different HMRC-recognized categories.
 * </p>
 *
 * @param totals immutable map of expense categories to their total amounts
 */
public record CategoryTotals(Map<ExpenseCategory, Money> totals) {

    /**
     * Creates empty category totals.
     *
     * @return an empty CategoryTotals instance
     */
    public static CategoryTotals empty() {
        return new CategoryTotals(Collections.emptyMap());
    }

    /**
     * Creates category totals from a map of categories to amounts.
     *
     * @param totals map of expense categories to their total amounts
     * @return a new CategoryTotals instance
     * @throws IllegalArgumentException if totals map is null
     */
    public static CategoryTotals of(Map<ExpenseCategory, Money> totals) {
        if (totals == null) {
            throw new IllegalArgumentException("Category totals map cannot be null");
        }
        return new CategoryTotals(Map.copyOf(totals));
    }

    /**
     * Returns the total amount for a specific category.
     * If the category has no expenses, returns zero.
     *
     * @param category the expense category
     * @return the total amount for the category, or zero if no expenses
     * @throws IllegalArgumentException if category is null
     */
    public Money totalFor(ExpenseCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return totals.getOrDefault(category, Money.gbp("0.00"));
    }

    /**
     * Returns the overall total across all categories.
     *
     * @return the sum of all category totals
     */
    public Money overallTotal() {
        return totals.values().stream()
                .reduce(Money.gbp("0.00"), Money::add);
    }

    /**
     * Returns all categories that have expenses.
     *
     * @return set of categories with non-zero totals
     */
    public Set<ExpenseCategory> categories() {
        return totals.keySet();
    }

    /**
     * Checks if there are no category totals.
     *
     * @return true if empty, false otherwise
     */
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
