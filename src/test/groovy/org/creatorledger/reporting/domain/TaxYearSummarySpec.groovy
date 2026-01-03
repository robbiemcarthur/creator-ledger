package org.creatorledger.reporting.domain

import org.creatorledger.common.Money
import org.creatorledger.common.TaxYearSummaryId
import org.creatorledger.common.UserId
import org.creatorledger.expense.api.ExpenseCategory
import spock.lang.Specification

class TaxYearSummarySpec extends Specification {

    def "should generate a tax year summary"() {
        given: "user ID, tax year, income, expenses, and category totals"
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2024)
        def totalIncome = Money.gbp("50000.00")
        def totalExpenses = Money.gbp("15000.00")
        def categoryTotals = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("10000.00"),
                (ExpenseCategory.SOFTWARE): Money.gbp("5000.00")
        ])

        when: "a tax year summary is generated"
        def summary = TaxYearSummary.generate(userId, taxYear, totalIncome, totalExpenses, categoryTotals)

        then: "it is created successfully"
        summary != null
        summary.id() != null
        summary.userId() == userId
        summary.taxYear() == taxYear
        summary.totalIncome() == totalIncome
        summary.totalExpenses() == totalExpenses
        summary.categoryTotals() == categoryTotals
    }

    def "should generate a tax year summary with specific ID"() {
        given: "all required parameters including a specific ID"
        def id = TaxYearSummaryId.generate()
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2024)
        def totalIncome = Money.gbp("50000.00")
        def totalExpenses = Money.gbp("15000.00")
        def categoryTotals = CategoryTotals.empty()

        when: "a tax year summary is generated with a specific ID"
        def summary = TaxYearSummary.generate(id, userId, taxYear, totalIncome, totalExpenses, categoryTotals)

        then: "it uses the provided ID"
        summary.id() == id
    }

    def "should calculate profit correctly"() {
        given: "a tax year summary with income and expenses"
        def summary = TaxYearSummary.generate(
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("50000.00"),
                Money.gbp("15000.00"),
                CategoryTotals.empty()
        )

        when: "profit is calculated"
        def profit = summary.profit()

        then: "it equals income minus expenses"
        profit == Money.gbp("35000.00")
    }

    def "should calculate profit as zero when income equals expenses"() {
        given: "a tax year summary with equal income and expenses"
        def amount = Money.gbp("25000.00")
        def summary = TaxYearSummary.generate(
                UserId.generate(),
                TaxYear.of(2024),
                amount,
                amount,
                CategoryTotals.empty()
        )

        when: "profit is calculated"
        def profit = summary.profit()

        then: "it is zero"
        profit == Money.gbp("0.00")
    }

    def "should calculate negative profit when expenses exceed income"() {
        given: "a tax year summary with expenses exceeding income"
        def summary = TaxYearSummary.generate(
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("10000.00"),
                Money.gbp("15000.00"),
                CategoryTotals.empty()
        )

        when: "profit is calculated"
        def profit = summary.profit()

        then: "it is negative"
        profit.amount() == new BigDecimal("-5000.00")
        profit.currency() == "GBP"
    }

    def "should reject null user ID"() {
        when: "generating with null user ID"
        TaxYearSummary.generate(
                null,
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "User ID cannot be null"
    }

    def "should reject null tax year"() {
        when: "generating with null tax year"
        TaxYearSummary.generate(
                UserId.generate(),
                null,
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Tax year cannot be null"
    }

    def "should reject null total income"() {
        when: "generating with null total income"
        TaxYearSummary.generate(
                UserId.generate(),
                TaxYear.of(2024),
                null,
                Money.gbp("500.00"),
                CategoryTotals.empty()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Total income cannot be null"
    }

    def "should reject null total expenses"() {
        when: "generating with null total expenses"
        TaxYearSummary.generate(
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                null,
                CategoryTotals.empty()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Total expenses cannot be null"
    }

    def "should reject null category totals"() {
        when: "generating with null category totals"
        TaxYearSummary.generate(
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                null
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Category totals cannot be null"
    }

    def "should reject null ID when provided"() {
        when: "generating with null ID"
        TaxYearSummary.generate(
                null,
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("1000.00"),
                Money.gbp("500.00"),
                CategoryTotals.empty()
        )

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Tax year summary ID cannot be null"
    }

    def "should be equal when IDs are the same"() {
        given: "two summaries with the same ID but different data"
        def id = TaxYearSummaryId.generate()
        def summary1 = TaxYearSummary.generate(
                id,
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("50000.00"),
                Money.gbp("15000.00"),
                CategoryTotals.empty()
        )
        def summary2 = TaxYearSummary.generate(
                id,
                UserId.generate(),
                TaxYear.of(2023),
                Money.gbp("30000.00"),
                Money.gbp("10000.00"),
                CategoryTotals.empty()
        )

        expect: "they are equal (entity equality based on ID)"
        summary1 == summary2
        summary1.hashCode() == summary2.hashCode()
    }

    def "should not be equal when IDs differ"() {
        given: "two summaries with different IDs"
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2024)
        def summary1 = TaxYearSummary.generate(
                userId,
                taxYear,
                Money.gbp("50000.00"),
                Money.gbp("15000.00"),
                CategoryTotals.empty()
        )
        def summary2 = TaxYearSummary.generate(
                userId,
                taxYear,
                Money.gbp("50000.00"),
                Money.gbp("15000.00"),
                CategoryTotals.empty()
        )

        expect: "they are not equal"
        summary1 != summary2
    }

    def "should provide readable toString"() {
        given: "a tax year summary"
        def summary = TaxYearSummary.generate(
                UserId.generate(),
                TaxYear.of(2024),
                Money.gbp("50000.00"),
                Money.gbp("15000.00"),
                CategoryTotals.empty()
        )

        when: "toString is called"
        def result = summary.toString()

        then: "it contains key information"
        result.contains("TaxYearSummary")
        result.contains("2024")
    }
}
