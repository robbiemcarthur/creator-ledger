package org.creatorledger.reporting.domain;

import org.creatorledger.common.Money;
import org.creatorledger.user.api.UserId;
import org.creatorledger.reporting.api.TaxYearSummaryId;

import java.time.Instant;

/**
 * Domain event representing that a tax year summary has been generated.
 * <p>
 * This event is published when a tax year summary is created, containing all the
 * financial data for a user's tax year including income, expenses, profit, and
 * category breakdowns. Other modules can listen to this event to perform related
 * actions such as generating reports or sending notifications.
 * </p>
 *
 * @param summaryId       the ID of the generated tax year summary
 * @param userId          the user this summary belongs to
 * @param taxYear         the UK tax year this summary covers
 * @param totalIncome     total income for the tax year
 * @param totalExpenses   total expenses for the tax year
 * @param profit          calculated profit (income - expenses)
 * @param categoryTotals  breakdown of expenses by category
 * @param occurredAt      when the event occurred
 */
public record TaxYearSummaryGenerated(
        TaxYearSummaryId summaryId,
        UserId userId,
        TaxYear taxYear,
        Money totalIncome,
        Money totalExpenses,
        Money profit,
        CategoryTotals categoryTotals,
        Instant occurredAt
) {

    public TaxYearSummaryGenerated {
        if (summaryId == null) {
            throw new IllegalArgumentException("Summary ID cannot be null");
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
        if (profit == null) {
            throw new IllegalArgumentException("Profit cannot be null");
        }
        if (categoryTotals == null) {
            throw new IllegalArgumentException("Category totals cannot be null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
    }

    @Override
    public String toString() {
        return "TaxYearSummaryGenerated[summaryId=%s, userId=%s, taxYear=%s, totalIncome=%s, totalExpenses=%s, profit=%s, occurredAt=%s]"
                .formatted(summaryId, userId, taxYear, totalIncome, totalExpenses, profit, occurredAt);
    }
}
