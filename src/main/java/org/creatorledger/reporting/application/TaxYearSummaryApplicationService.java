package org.creatorledger.reporting.application;

import org.creatorledger.common.Money;
import org.creatorledger.expense.api.ExpenseCategory;
import org.creatorledger.expense.api.ExpenseData;
import org.creatorledger.expense.api.ExpenseQueryService;
import org.creatorledger.income.api.IncomeData;
import org.creatorledger.income.api.IncomeQueryService;
import org.creatorledger.reporting.api.TaxYearSummaryId;
import org.creatorledger.reporting.domain.CategoryTotals;
import org.creatorledger.reporting.domain.TaxYearSummary;
import org.creatorledger.reporting.domain.TaxYearSummaryGenerated;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaxYearSummaryApplicationService {

    private final IncomeQueryService incomeQueryService;
    private final ExpenseQueryService expenseQueryService;
    private final TaxYearSummaryRepository taxYearSummaryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TaxYearSummaryApplicationService(
            final IncomeQueryService incomeQueryService,
            final ExpenseQueryService expenseQueryService,
            final TaxYearSummaryRepository taxYearSummaryRepository,
            final ApplicationEventPublisher eventPublisher
    ) {
        if (incomeQueryService == null) {
            throw new IllegalArgumentException("Income query service cannot be null");
        }
        if (expenseQueryService == null) {
            throw new IllegalArgumentException("Expense query service cannot be null");
        }
        if (taxYearSummaryRepository == null) {
            throw new IllegalArgumentException("Tax year summary repository cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("Event publisher cannot be null");
        }
        this.incomeQueryService = incomeQueryService;
        this.expenseQueryService = expenseQueryService;
        this.taxYearSummaryRepository = taxYearSummaryRepository;
        this.eventPublisher = eventPublisher;
    }

    public TaxYearSummaryId generate(final GenerateTaxYearSummaryCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        final List<IncomeData> incomes = incomeQueryService.findByUserIdAndDateRange(
                command.userId(),
                command.taxYear().startDate(),
                command.taxYear().endDate()
        );

        final List<ExpenseData> expenses = expenseQueryService.findByUserIdAndDateRange(
                command.userId(),
                command.taxYear().startDate(),
                command.taxYear().endDate()
        );

        final Money totalIncome = calculateTotalIncome(incomes);
        final Money totalExpenses = calculateTotalExpenses(expenses);
        final CategoryTotals categoryTotals = calculateCategoryTotals(expenses);

        final TaxYearSummary summary = TaxYearSummary.generate(
                command.userId(),
                command.taxYear(),
                totalIncome,
                totalExpenses,
                categoryTotals
        );

        taxYearSummaryRepository.save(summary);

        // Publish domain event
        final TaxYearSummaryGenerated event = new TaxYearSummaryGenerated(
            summary.id(),
            summary.userId(),
            summary.taxYear(),
            summary.totalIncome(),
            summary.totalExpenses(),
            summary.profit(),
            summary.categoryTotals(),
            Instant.now()
        );
        eventPublisher.publishEvent(event);

        return summary.id();
    }

    public Optional<TaxYearSummary> findById(final TaxYearSummaryId id) {
        if (id == null) {
            throw new IllegalArgumentException("Tax year summary ID cannot be null");
        }
        return taxYearSummaryRepository.findById(id);
    }

    private Money calculateTotalIncome(final List<IncomeData> incomes) {
        return incomes.stream()
                .map(IncomeData::amount)
                .reduce(Money.gbp("0.00"), Money::add);
    }

    private Money calculateTotalExpenses(final List<ExpenseData> expenses) {
        return expenses.stream()
                .map(ExpenseData::amount)
                .reduce(Money.gbp("0.00"), Money::add);
    }

    private CategoryTotals calculateCategoryTotals(final List<ExpenseData> expenses) {
        if (expenses.isEmpty()) {
            return CategoryTotals.empty();
        }

        final Map<ExpenseCategory, Money> totals = new HashMap<>();
        for (final ExpenseData expense : expenses) {
            final ExpenseCategory category = expense.category();
            final Money currentTotal = totals.getOrDefault(category, Money.gbp("0.00"));
            totals.put(category, currentTotal.add(expense.amount()));
        }

        return CategoryTotals.of(totals);
    }
}
