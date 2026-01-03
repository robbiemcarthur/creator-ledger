package org.creatorledger.reporting.domain

import org.creatorledger.common.Money
import org.creatorledger.expense.api.ExpenseCategory
import spock.lang.Specification

class CategoryTotalsSpec extends Specification {

    def "should create empty category totals"() {
        when: "empty category totals are created"
        def totals = CategoryTotals.empty()

        then: "it is created successfully"
        totals != null
        totals.isEmpty()
    }

    def "should create category totals from a map"() {
        given: "a map of categories to money"
        def map = [
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00"),
                (ExpenseCategory.SOFTWARE): Money.gbp("500.00")
        ]

        when: "category totals are created"
        def totals = CategoryTotals.of(map)

        then: "it is created successfully"
        totals != null
        !totals.isEmpty()
    }

    def "should get total for a specific category"() {
        given: "category totals with equipment and software"
        def totals = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00"),
                (ExpenseCategory.SOFTWARE): Money.gbp("500.00")
        ])

        when: "getting total for equipment"
        def equipmentTotal = totals.totalFor(ExpenseCategory.EQUIPMENT)

        then: "it returns the correct amount"
        equipmentTotal == Money.gbp("1000.00")
    }

    def "should return zero for category with no expenses"() {
        given: "category totals with only equipment"
        def totals = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00")
        ])

        when: "getting total for software"
        def softwareTotal = totals.totalFor(ExpenseCategory.SOFTWARE)

        then: "it returns zero"
        softwareTotal == Money.gbp("0.00")
    }

    def "should calculate overall total across all categories"() {
        given: "category totals with multiple categories"
        def totals = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00"),
                (ExpenseCategory.SOFTWARE): Money.gbp("500.00"),
                (ExpenseCategory.TRAVEL): Money.gbp("250.50")
        ])

        when: "getting the overall total"
        def overallTotal = totals.overallTotal()

        then: "it returns the sum of all categories"
        overallTotal == Money.gbp("1750.50")
    }

    def "should return zero for overall total when empty"() {
        given: "empty category totals"
        def totals = CategoryTotals.empty()

        when: "getting the overall total"
        def overallTotal = totals.overallTotal()

        then: "it returns zero"
        overallTotal == Money.gbp("0.00")
    }

    def "should reject null map"() {
        when: "creating with null map"
        CategoryTotals.of(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Category totals map cannot be null"
    }

    def "should reject null category when getting total"() {
        given: "valid category totals"
        def totals = CategoryTotals.empty()

        when: "getting total for null category"
        totals.totalFor(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Category cannot be null"
    }

    def "should get all categories with expenses"() {
        given: "category totals with multiple categories"
        def totals = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00"),
                (ExpenseCategory.SOFTWARE): Money.gbp("500.00")
        ])

        when: "getting all categories"
        def categories = totals.categories()

        then: "it returns all categories with expenses"
        categories.size() == 2
        categories.contains(ExpenseCategory.EQUIPMENT)
        categories.contains(ExpenseCategory.SOFTWARE)
    }

    def "should be equal when maps are equal"() {
        given: "two category totals with the same data"
        def totals1 = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00")
        ])
        def totals2 = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00")
        ])

        expect: "they are equal"
        totals1 == totals2
        totals1.hashCode() == totals2.hashCode()
    }

    def "should not be equal when maps differ"() {
        given: "two category totals with different data"
        def totals1 = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00")
        ])
        def totals2 = CategoryTotals.of([
                (ExpenseCategory.SOFTWARE): Money.gbp("1000.00")
        ])

        expect: "they are not equal"
        totals1 != totals2
    }

    def "should provide readable toString"() {
        given: "category totals with data"
        def totals = CategoryTotals.of([
                (ExpenseCategory.EQUIPMENT): Money.gbp("1000.00"),
                (ExpenseCategory.SOFTWARE): Money.gbp("500.00")
        ])

        when: "toString is called"
        def result = totals.toString()

        then: "it contains category information"
        result.contains("CategoryTotals")
        result.contains("EQUIPMENT")
        result.contains("SOFTWARE")
    }
}
