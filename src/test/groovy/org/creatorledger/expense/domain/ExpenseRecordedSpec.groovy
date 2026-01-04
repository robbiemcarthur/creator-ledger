package org.creatorledger.expense.domain

import org.creatorledger.expense.api.ExpenseId
import org.creatorledger.common.Money
import org.creatorledger.user.api.UserId
import org.creatorledger.expense.api.ExpenseCategory
import spock.lang.Specification
import java.time.Instant
import java.time.LocalDate

class ExpenseRecordedSpec extends Specification {

    def "should create ExpenseRecorded event"() {
        given: "expense details"
        def expenseId = ExpenseId.generate()
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def category = ExpenseCategory.EQUIPMENT
        def description = "MacBook Pro laptop"
        def incurredDate = LocalDate.of(2026, 1, 15)
        def occurredAt = Instant.now()

        when: "creating an ExpenseRecorded event"
        def event = new ExpenseRecorded(expenseId, userId, amount, category, description, incurredDate, occurredAt)

        then: "it should contain all the data"
        event.expenseId() == expenseId
        event.userId() == userId
        event.amount() == amount
        event.category() == category
        event.description() == description
        event.incurredDate() == incurredDate
        event.occurredAt() == occurredAt
    }

    def "should create ExpenseRecorded event with current timestamp"() {
        given: "expense details"
        def expenseId = ExpenseId.generate()
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def category = ExpenseCategory.SOFTWARE
        def description = "Adobe Creative Cloud subscription"
        def incurredDate = LocalDate.of(2026, 1, 15)
        def before = Instant.now()

        when: "creating an ExpenseRecorded event without timestamp"
        def event = ExpenseRecorded.of(expenseId, userId, amount, category, description, incurredDate)
        def after = Instant.now()

        then: "it should have a timestamp between before and after"
        event.expenseId() == expenseId
        event.userId() == userId
        event.amount() == amount
        event.category() == category
        event.description() == description
        event.incurredDate() == incurredDate
        !event.occurredAt().isBefore(before)
        !event.occurredAt().isAfter(after)
    }

    def "should be equal when all fields are equal"() {
        given: "the same expense data"
        def expenseId = ExpenseId.generate()
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def category = ExpenseCategory.TRAVEL
        def description = "Train tickets to London"
        def incurredDate = LocalDate.of(2026, 1, 15)
        def occurredAt = Instant.now()

        when: "creating two events with same data"
        def event1 = new ExpenseRecorded(expenseId, userId, amount, category, description, incurredDate, occurredAt)
        def event2 = new ExpenseRecorded(expenseId, userId, amount, category, description, incurredDate, occurredAt)

        then: "they should be equal"
        event1 == event2
        event1.hashCode() == event2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "an ExpenseRecorded event"
        def expenseId = ExpenseId.generate()
        def userId = UserId.generate()
        def amount = Money.gbp("150.00")
        def category = ExpenseCategory.EQUIPMENT
        def description = "MacBook Pro laptop"
        def incurredDate = LocalDate.of(2026, 1, 15)
        def event = ExpenseRecorded.of(expenseId, userId, amount, category, description, incurredDate)

        when: "converting to string"
        def result = event.toString()

        then: "it should contain key information"
        result.contains(expenseId.toString())
        result.contains("150.00")
        result.contains("GBP")
        result.contains("EQUIPMENT")
    }
}
