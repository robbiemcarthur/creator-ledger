package org.creatorledger.reporting.domain;

import org.creatorledger.common.Money;
import org.creatorledger.reporting.api.TaxYearSummaryId;
import org.creatorledger.user.api.UserId;

public record TaxYearSummary(
        TaxYearSummaryId id,
        UserId userId,
        TaxYear taxYear,
        Money totalIncome,
        Money totalExpenses,
        CategoryTotals categoryTotals
) {

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

    public Money profit() {
        if (totalIncome.isGreaterThan(totalExpenses) || totalIncome.equals(totalExpenses)) {
            return totalIncome.subtract(totalExpenses);
        }

        java.math.BigDecimal difference = totalIncome.amount().subtract(totalExpenses.amount());
        return new Money(difference.setScale(2, java.math.RoundingMode.HALF_DOWN), totalIncome.currency());
    }

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
