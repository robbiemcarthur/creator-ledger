package org.creatorledger.reporting.application

import org.creatorledger.common.Money
import org.creatorledger.event.api.EventId
import org.creatorledger.expense.api.ExpenseCategory
import org.creatorledger.expense.api.ExpenseData
import org.creatorledger.expense.api.ExpenseQueryService
import org.creatorledger.expense.domain.Expense
import org.creatorledger.income.api.IncomeData
import org.creatorledger.income.api.IncomeQueryService
import org.creatorledger.income.domain.Income
import org.creatorledger.reporting.domain.TaxYear
import org.creatorledger.reporting.domain.TaxYearSummaryGenerated
import org.creatorledger.user.api.UserId
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

import java.time.LocalDate

class TaxYearSummaryApplicationServiceSpec extends Specification {

    IncomeQueryService incomeQueryService
    ExpenseQueryService expenseQueryService
    TaxYearSummaryRepository taxYearSummaryRepository
    ApplicationEventPublisher eventPublisher
    TaxYearSummaryApplicationService service

    def setup() {
        incomeQueryService = Mock(IncomeQueryService)
        expenseQueryService = Mock(ExpenseQueryService)
        taxYearSummaryRepository = Mock(TaxYearSummaryRepository)
        eventPublisher = Mock(ApplicationEventPublisher)
        service = new TaxYearSummaryApplicationService(
                incomeQueryService,
                expenseQueryService,
                taxYearSummaryRepository,
                eventPublisher
        )
    }

    def "should generate tax year summary with income and expenses"() {
        given: "a user and tax year"
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2025)
        def command = new GenerateTaxYearSummaryCommand(userId, taxYear)

        and: "some income for the tax year"
        def income1 = IncomeData.from(Income.record(userId, EventId.generate(), Money.gbp("1000.00"), "Project 1", LocalDate.of(2025, 5, 1)))
        def income2 = IncomeData.from(Income.record(userId, EventId.generate(), Money.gbp("1500.00"), "Project 2", LocalDate.of(2025, 7, 15)))
        incomeQueryService.findByUserIdAndDateRange(userId, taxYear.startDate(), taxYear.endDate()) >> [income1, income2]

        and: "some expenses for the tax year"
        def expense1 = ExpenseData.from(Expense.record(userId, Money.gbp("300.00"), ExpenseCategory.EQUIPMENT, "Laptop", LocalDate.of(2025, 6, 1)))
        def expense2 = ExpenseData.from(Expense.record(userId, Money.gbp("200.00"), ExpenseCategory.SOFTWARE, "Software license", LocalDate.of(2025, 8, 1)))
        expenseQueryService.findByUserIdAndDateRange(userId, taxYear.startDate(), taxYear.endDate()) >> [expense1, expense2]

        when: "generating the summary"
        def summaryId = service.generate(command)

        then: "the summary is saved with correct totals"
        1 * taxYearSummaryRepository.save(_) >> { args ->
            def summary = args[0]
            assert summary.userId() == userId
            assert summary.taxYear() == taxYear
            assert summary.totalIncome() == Money.gbp("2500.00")
            assert summary.totalExpenses() == Money.gbp("500.00")
            assert summary.profit() == Money.gbp("2000.00")
            assert summary.categoryTotals().totalFor(ExpenseCategory.EQUIPMENT) == Money.gbp("300.00")
            assert summary.categoryTotals().totalFor(ExpenseCategory.SOFTWARE) == Money.gbp("200.00")
            return summary
        }
        summaryId != null
    }

    def "should publish TaxYearSummaryGenerated event when generating summary"() {
        given: "a user and tax year"
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2025)
        def command = new GenerateTaxYearSummaryCommand(userId, taxYear)

        and: "some income for the tax year"
        def income = IncomeData.from(Income.record(userId, EventId.generate(), Money.gbp("1000.00"), "Project", LocalDate.of(2025, 5, 1)))
        incomeQueryService.findByUserIdAndDateRange(userId, taxYear.startDate(), taxYear.endDate()) >> [income]

        and: "some expenses for the tax year"
        def expense = ExpenseData.from(Expense.record(userId, Money.gbp("300.00"), ExpenseCategory.EQUIPMENT, "Laptop", LocalDate.of(2025, 6, 1)))
        expenseQueryService.findByUserIdAndDateRange(userId, taxYear.startDate(), taxYear.endDate()) >> [expense]

        when: "generating the summary"
        service.generate(command)

        then: "the summary is saved"
        1 * taxYearSummaryRepository.save(_) >> { args -> args[0] }

        and: "TaxYearSummaryGenerated event is published"
        1 * eventPublisher.publishEvent(_) >> { arguments ->
            def event = arguments[0]
            assert event instanceof TaxYearSummaryGenerated
            assert event.userId() == userId
            assert event.taxYear() == taxYear
            assert event.totalIncome() == Money.gbp("1000.00")
            assert event.totalExpenses() == Money.gbp("300.00")
            assert event.profit() == Money.gbp("700.00")
            assert event.occurredAt() != null
        }
    }

    def "should generate summary with zero income"() {
        given: "a user and tax year with no income"
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2025)
        def command = new GenerateTaxYearSummaryCommand(userId, taxYear)

        incomeQueryService.findByUserIdAndDateRange(_, _, _) >> []

        and: "some expenses"
        def expense = ExpenseData.from(Expense.record(userId, Money.gbp("100.00"), ExpenseCategory.EQUIPMENT, "Item", LocalDate.of(2025, 6, 1)))
        expenseQueryService.findByUserIdAndDateRange(_, _, _) >> [expense]

        when: "generating the summary"
        service.generate(command)

        then: "total income is zero"
        1 * taxYearSummaryRepository.save(_) >> { args ->
            def summary = args[0]
            assert summary.totalIncome() == Money.gbp("0.00")
            assert summary.totalExpenses() == Money.gbp("100.00")
            return summary
        }
    }

    def "should generate summary with zero expenses"() {
        given: "a user and tax year with no expenses"
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2025)
        def command = new GenerateTaxYearSummaryCommand(userId, taxYear)

        and: "some income"
        def income = IncomeData.from(Income.record(userId, EventId.generate(), Money.gbp("1000.00"), "Project", LocalDate.of(2025, 5, 1)))
        incomeQueryService.findByUserIdAndDateRange(_, _, _) >> [income]

        expenseQueryService.findByUserIdAndDateRange(_, _, _) >> []

        when: "generating the summary"
        service.generate(command)

        then: "total expenses is zero"
        1 * taxYearSummaryRepository.save(_) >> { args ->
            def summary = args[0]
            assert summary.totalIncome() == Money.gbp("1000.00")
            assert summary.totalExpenses() == Money.gbp("0.00")
            assert summary.categoryTotals().isEmpty()
            return summary
        }
    }

    def "should reject null command"() {
        when: "generating with null command"
        service.generate(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should find summary by ID"() {
        given: "an existing summary"
        def summaryId = org.creatorledger.reporting.api.TaxYearSummaryId.generate()
        def summary = org.creatorledger.reporting.domain.TaxYearSummary.generate(
                summaryId,
                UserId.generate(),
                TaxYear.of(2025),
                Money.gbp("2000.00"),
                Money.gbp("500.00"),
                org.creatorledger.reporting.domain.CategoryTotals.empty()
        )

        when: "finding by ID"
        def result = service.findById(summaryId)

        then: "the repository is queried"
        1 * taxYearSummaryRepository.findById(summaryId) >> Optional.of(summary)
        result.isPresent()
        result.get() == summary
    }

    def "should reject null ID when finding"() {
        when: "finding with null ID"
        service.findById(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Tax year summary ID cannot be null"
    }
}
