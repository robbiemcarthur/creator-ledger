package org.creatorledger.reporting.domain

import org.creatorledger.common.Money
import org.creatorledger.common.TaxYearSummaryId
import org.creatorledger.common.UserId
import org.creatorledger.expense.api.ExpenseCategory
import spock.lang.Specification

import java.time.Instant

class TaxYearSummaryGeneratedSpec extends Specification {

    def "should create a valid tax year summary generated event"() {
        given: "valid event parameters"
        def summaryId = TaxYearSummaryId.generate()
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2024)
        def totalIncome = Money.gbp("50000.00")
        def totalExpenses = Money.gbp("15000.00")
        def profit = Money.gbp("35000.00")
        def categoryTotals = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("10000.00"),
                (ExpenseCategory.SOFTWARE): Money.gbp("5000.00")
        ])
        def occurredAt = Instant.now()

        when: "the event is created"
        def event = new TaxYearSummaryGenerated(
                summaryId,
                userId,
                taxYear,
                totalIncome,
                totalExpenses,
                profit,
                categoryTotals,
                occurredAt
        )

        then: "it is created successfully"
        event.summaryId() == summaryId
        event.userId() == userId
        event.taxYear() == taxYear
        event.totalIncome() == totalIncome
        event.totalExpenses() == totalExpenses
        event.profit() == profit
        event.categoryTotals() == categoryTotals
        event.occurredAt() == occurredAt
    }

    def "should reject null summary ID"() {
        when: "creating event with null summary ID"
        new TaxYearSummaryGenerated(
                null,
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty(),
                Instant.now()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Summary ID cannot be null"
    }

    def "should reject null user ID"() {
        when: "creating event with null user ID"
        new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                null,
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty(),
                Instant.now()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "User ID cannot be null"
    }

    def "should reject null tax year"() {
        when: "creating event with null tax year"
        new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                UserId.generate(),
                null,
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty(),
                Instant.now()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Tax year cannot be null"
    }

    def "should reject null total income"() {
        when: "creating event with null total income"
        new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                UserId.generate(),
                TaxYear.of(2024),
                null,
                Money.gbp("500.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty(),
                Instant.now()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Total income cannot be null"
    }

    def "should reject null total expenses"() {
        when: "creating event with null total expenses"
        new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                null,
                Money.gbp("500.00"),
                CategoryTotals.empty(),
                Instant.now()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Total expenses cannot be null"
    }

    def "should reject null profit"() {
        when: "creating event with null profit"
        new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                null,
                CategoryTotals.empty(),
                Instant.now()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Profit cannot be null"
    }

    def "should reject null category totals"() {
        when: "creating event with null category totals"
        new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                Money.gbp("500.00"),
                null,
                Instant.now()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Category totals cannot be null"
    }

    def "should reject null occurred at"() {
        when: "creating event with null occurred at"
        new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty(),
                null
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Occurred at cannot be null"
    }

    def "should provide readable toString"() {
        given: "a tax year summary generated event"
        def event = new TaxYearSummaryGenerated(
                TaxYearSummaryId.generate(),
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("50000.00"),
                Money.gbp("15000.00"),
                Money.gbp("35000.00"),
                CategoryTotals.empty(),
                Instant.now()
        )

        when: "toString is called"
        def result = event.toString()

        then: "it contains key information"
        result.contains("TaxYearSummaryGenerated")
        result.contains("2024")
    }
}
