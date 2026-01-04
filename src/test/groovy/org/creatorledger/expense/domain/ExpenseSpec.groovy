package org.creatorledger.expense.domain

import org.creatorledger.expense.api.ExpenseId
import org.creatorledger.common.Money
import org.creatorledger.user.api.UserId
import org.creatorledger.expense.api.ExpenseCategory
import spock.lang.Specification
import java.time.LocalDate

class ExpenseSpec extends Specification {

    def "should record new expense with generated ID"() {
        given: "valid expense details"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def category = ExpenseCategory.EQUIPMENT
        def description = "MacBook Pro laptop"
        def incurredDate = LocalDate.of(2026, 1, 15)

        when: "recording new expense"
        def expense = Expense.record(userId, amount, category, description, incurredDate)

        then: "the expense should have all the data"
        expense.id() != null
        expense.userId() == userId
        expense.amount() == amount
        expense.category() == category
        expense.description() == description
        expense.incurredDate() == incurredDate
    }

    def "should record new expense with specific ID"() {
        given: "valid expense details with specific ID"
        def expenseId = ExpenseId.generate()
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def category = ExpenseCategory.SOFTWARE
        def description = "Adobe Creative Cloud subscription"
        def incurredDate = LocalDate.of(2026, 1, 15)

        when: "recording expense with specific ID"
        def expense = Expense.record(expenseId, userId, amount, category, description, incurredDate)

        then: "the expense should have the specified ID"
        expense.id() == expenseId
        expense.userId() == userId
        expense.amount() == amount
        expense.category() == category
    }

    def "should reject null expenseId when recording"() {
        given: "null expenseId"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")

        when: "recording expense with null expenseId"
        Expense.record(null, userId, amount, ExpenseCategory.EQUIPMENT, "Description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "ExpenseId cannot be null"
    }

    def "should reject null userId when recording"() {
        given: "null userId"
        def amount = Money.gbp("150.00")

        when: "recording expense with null userId"
        Expense.record(null, amount, ExpenseCategory.EQUIPMENT, "Description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "UserId cannot be null"
    }

    def "should reject null amount when recording"() {
        given: "null amount"
        def userId = UserId.generate()

        when: "recording expense with null amount"
        Expense.record(userId, null, ExpenseCategory.EQUIPMENT, "Description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Amount cannot be null"
    }

    def "should reject null category when recording"() {
        given: "null category"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")

        when: "recording expense with null category"
        Expense.record(userId, amount, null, "Description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Category cannot be null"
    }

    def "should reject null description when recording"() {
        given: "null description"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")

        when: "recording expense with null description"
        Expense.record(userId, amount, ExpenseCategory.EQUIPMENT, null, LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Description cannot be null or blank"
    }

    def "should reject blank description when recording"() {
        given: "blank description"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")

        when: "recording expense with blank description"
        Expense.record(userId, amount, ExpenseCategory.EQUIPMENT, "   ", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Description cannot be null or blank"
    }

    def "should reject null incurredDate when recording"() {
        given: "null incurredDate"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")

        when: "recording expense with null incurredDate"
        Expense.record(userId, amount, ExpenseCategory.EQUIPMENT, "Description", null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "IncurredDate cannot be null"
    }

    def "should trim description when recording"() {
        given: "description with whitespace"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def description = "  MacBook Pro laptop  "

        when: "recording expense"
        def expense = Expense.record(userId, amount, ExpenseCategory.EQUIPMENT, description, LocalDate.now())

        then: "description should be trimmed"
        expense.description() == "MacBook Pro laptop"
    }

    def "should update expense details"() {
        given: "an existing expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Original description",
            LocalDate.of(2026, 1, 15)
        )
        def newAmount = Money.gbp("200.00")
        def newCategory = ExpenseCategory.SOFTWARE
        def newDescription = "Updated description"
        def newDate = LocalDate.of(2026, 2, 20)

        when: "updating the expense"
        def updated = expense.update(newAmount, newCategory, newDescription, newDate)

        then: "the expense should have updated values but same ID and userId"
        updated.id() == expense.id()
        updated.userId() == expense.userId()
        updated.amount() == newAmount
        updated.category() == newCategory
        updated.description() == newDescription
        updated.incurredDate() == newDate
    }

    def "should reject null amount when updating"() {
        given: "an existing expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Description",
            LocalDate.now()
        )

        when: "updating with null amount"
        expense.update(null, ExpenseCategory.SOFTWARE, "New description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Amount cannot be null"
    }

    def "should reject null category when updating"() {
        given: "an existing expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Description",
            LocalDate.now()
        )

        when: "updating with null category"
        expense.update(Money.gbp("200.00"), null, "New description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Category cannot be null"
    }

    def "should reject blank description when updating"() {
        given: "an existing expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Description",
            LocalDate.now()
        )

        when: "updating with blank description"
        expense.update(Money.gbp("200.00"), ExpenseCategory.SOFTWARE, "   ", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Description cannot be null or blank"
    }

    def "should reject null incurredDate when updating"() {
        given: "an existing expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Description",
            LocalDate.now()
        )

        when: "updating with null incurredDate"
        expense.update(Money.gbp("200.00"), ExpenseCategory.SOFTWARE, "New description", null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "IncurredDate cannot be null"
    }

    def "should trim description when updating"() {
        given: "an existing expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Original description",
            LocalDate.now()
        )

        when: "updating with description containing whitespace"
        def updated = expense.update(
            Money.gbp("200.00"),
            ExpenseCategory.SOFTWARE,
            "  Updated description  ",
            LocalDate.now()
        )

        then: "description should be trimmed"
        updated.description() == "Updated description"
    }

    def "should be equal when IDs are equal"() {
        given: "the same expense ID and different details"
        def expenseId = ExpenseId.generate()
        def expense1 = Expense.record(
            expenseId,
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Description 1",
            LocalDate.now()
        )
        def expense2 = Expense.record(
            expenseId,
            UserId.generate(),
            Money.gbp("300.00"),
            ExpenseCategory.SOFTWARE,
            "Description 2",
            LocalDate.now()
        )

        expect: "they should be equal (entity equality is based on ID)"
        expense1 == expense2
        expense1.hashCode() == expense2.hashCode()
    }

    def "should not be equal when IDs are different"() {
        given: "different expense IDs but same details"
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def category = ExpenseCategory.EQUIPMENT
        def description = "Same description"
        def date = LocalDate.now()

        when: "creating two expenses with different IDs"
        def expense1 = Expense.record(userId, amount, category, description, date)
        def expense2 = Expense.record(userId, amount, category, description, date)

        then: "they should not be equal"
        expense1 != expense2
    }

    def "should have meaningful toString"() {
        given: "an expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "MacBook Pro laptop",
            LocalDate.of(2026, 1, 15)
        )

        when: "converting to string"
        def result = expense.toString()

        then: "it should contain key information"
        result.contains(expense.id().toString())
        result.contains("150.00")
        result.contains("EQUIPMENT")
        result.contains("MacBook Pro laptop")
    }
}
