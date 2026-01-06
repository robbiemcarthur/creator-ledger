package org.creatorledger.reporting.infrastructure.web;

import org.creatorledger.expense.api.ExpenseCategory;
import org.creatorledger.reporting.domain.TaxYearSummary;

import java.util.Map;
import java.util.stream.Collectors;

public record TaxYearSummaryResponse(
    String id,
    String userId,
    Integer taxYear,
    MoneyDto totalIncome,
    MoneyDto totalExpenses,
    MoneyDto profit,
    Map<ExpenseCategory, MoneyDto> categoryTotals
) {

    public static TaxYearSummaryResponse from(final TaxYearSummary summary) {
        if (summary == null) {
            throw new IllegalArgumentException("Tax year summary cannot be null");
        }

        final Map<ExpenseCategory, MoneyDto> categoryTotalsDto = summary.categoryTotals().totals()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> MoneyDto.from(entry.getValue())
                ));

        return new TaxYearSummaryResponse(
                summary.id().value().toString(),
                summary.userId().value().toString(),
                summary.taxYear().startYear(),
                MoneyDto.from(summary.totalIncome()),
                MoneyDto.from(summary.totalExpenses()),
                MoneyDto.from(summary.profit()),
                categoryTotalsDto
        );
    }
}
