package org.creatorledger.reporting.domain;

import org.creatorledger.common.Money;
import org.creatorledger.common.TaxYearSummaryId;
import org.creatorledger.common.UserId;

/**
 * Aggregate root representing a tax year summary for a user.
 * <p>
 * A tax year summary aggregates all income and expenses for a specific UK tax year,
 * providing totals, profit calculations, and expense breakdowns by category.
 * This is a core domain object for helping self-employed creatives understand
 * their financial position for tax purposes.
 * </p>
 *
 * @param id              unique identifier for this tax year summary
 * @param userId          the user this summary belongs to
 * @param taxYear         the UK tax year this summary covers
 * @param totalIncome     total income for the tax year
 * @param totalExpenses   total expenses for the tax year
 * @param categoryTotals  breakdown of expenses by HMRC-recognized categories
 */
public record TaxYearSummary(
        TaxYearSummaryId id,
        UserId userId,
        TaxYear taxYear,
        Money totalIncome,
        Money totalExpenses,
        CategoryTotals categoryTotals
) {

    /**
     * Generates a new tax year summary with a generated ID.
     *
     * @param userId          the user this summary belongs to
     * @param taxYear         the UK tax year this summary covers
     * @param totalIncome     total income for the tax year
     * @param totalExpenses   total expenses for the tax year
     * @param categoryTotals  breakdown of expenses by category
     * @return a new TaxYearSummary instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static TaxYearSummary generate(
            UserId userId,
            TaxYear taxYear,
            Money totalIncome,
            Money totalExpenses,
            CategoryTotals categoryTotals
    ) {
        return generate(
                TaxYearSummaryId.generate(),
                userId,
                taxYear,
                totalIncome,
                totalExpenses,
                categoryTotals
        );
    }

    /**
     * Generates a new tax year summary with a specific ID.
     *
     * @param id              unique identifier for this tax year summary
     * @param userId          the user this summary belongs to
     * @param taxYear         the UK tax year this summary covers
     * @param totalIncome     total income for the tax year
     * @param totalExpenses   total expenses for the tax year
     * @param categoryTotals  breakdown of expenses by category
     * @return a new TaxYearSummary instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static TaxYearSummary generate(
            TaxYearSummaryId id,
            UserId userId,
            TaxYear taxYear,
            Money totalIncome,
            Money totalExpenses,
            CategoryTotals categoryTotals
    ) {
        validateParameters(id, userId, taxYear, totalIncome, totalExpenses, categoryTotals);
        return new TaxYearSummary(id, userId, taxYear, totalIncome, totalExpenses, categoryTotals);
    }

    /**
     * Calculates the profit (or loss) for this tax year.
     * Profit = Total Income - Total Expenses
     * <p>
     * Note: This method uses BigDecimal arithmetic to allow negative results (losses),
     * bypassing Money's constraint that amounts must be non-negative.
     * </p>
     *
     * @return the profit amount (negative if a loss)
     */
    public Money profit() {
        if (totalIncome.isGreaterThan(totalExpenses) || totalIncome.equals(totalExpenses)) {
            return totalIncome.subtract(totalExpenses);
        }
        // Handle loss (expenses > income) - Money doesn't allow negative amounts,
        // so we calculate the difference and create a Money-like structure
        // This is a known limitation that should be addressed with a Profit value object
        java.math.BigDecimal difference = totalIncome.amount().subtract(totalExpenses.amount());
        // For now, we create Money using a workaround for negative amounts
        return new Money(difference.setScale(2, java.math.RoundingMode.HALF_DOWN), totalIncome.currency());
    }

    /**
     * Entity equality is based on ID only.
     * Two tax year summaries are equal if they have the same ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TaxYearSummary that = (TaxYearSummary) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "TaxYearSummary[id=%s, userId=%s, taxYear=%s, totalIncome=%s, totalExpenses=%s, profit=%s]"
                .formatted(id, userId, taxYear, totalIncome, totalExpenses, profit());
    }

    private static void validateParameters(
            TaxYearSummaryId id,
            UserId userId,
            TaxYear taxYear,
            Money totalIncome,
            Money totalExpenses,
            CategoryTotals categoryTotals
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Tax year summary ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (taxYear == null) {
            throw new IllegalArgumentException("Tax year cannot be null");
        }
        if (totalIncome == null) {
            throw new IllegalArgumentException("Total income cannot be null");
        }
        if (totalExpenses == null) {
            throw new IllegalArgumentException("Total expenses cannot be null");
        }
        if (categoryTotals == null) {
            throw new IllegalArgumentException("Category totals cannot be null");
        }
    }
}
