package org.creatorledger.income.domain

import org.creatorledger.common.EventId
import org.creatorledger.common.IncomeId
import org.creatorledger.common.Money
import org.creatorledger.common.UserId
import spock.lang.Specification
import java.time.LocalDate

class IncomeSpec extends Specification {

    def "should record new income with generated ID"() {
        given: "valid income details"
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)

        when: "recording new income"
        def income = Income.record(userId, eventId, amount, description, receivedDate)

        then: "the income should have all the data"
        income.id() != null
        income.userId() == userId
        income.eventId() == eventId
        income.amount() == amount
        income.description() == description
        income.receivedDate() == receivedDate
        income.status() == PaymentStatus.PENDING
    }

    def "should record new income with specific ID"() {
        given: "valid income details with specific ID"
        def incomeId = IncomeId.generate()
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)

        when: "recording income with specific ID"
        def income = Income.record(incomeId, userId, eventId, amount, description, receivedDate)

        then: "the income should have the specified ID"
        income.id() == incomeId
        income.userId() == userId
        income.status() == PaymentStatus.PENDING
    }

    def "should reject null userId when recording"() {
        given: "null userId"
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")

        when: "recording income with null userId"
        Income.record(null, eventId, amount, "Description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "UserId cannot be null"
    }

    def "should reject null eventId when recording"() {
        given: "null eventId"
        def userId = UserId.generate()
        def amount = Money.gbp("500.00")

        when: "recording income with null eventId"
        Income.record(userId, null, amount, "Description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "EventId cannot be null"
    }

    def "should reject null amount when recording"() {
        given: "null amount"
        def userId = UserId.generate()
        def eventId = EventId.generate()

        when: "recording income with null amount"
        Income.record(userId, eventId, null, "Description", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Amount cannot be null"
    }

    def "should reject blank description when recording"() {
        given: "blank description"
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")

        when: "recording income with blank description"
        Income.record(userId, eventId, amount, "   ", LocalDate.now())

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Description cannot be null or blank"
    }

    def "should reject null receivedDate when recording"() {
        given: "null receivedDate"
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")

        when: "recording income with null receivedDate"
        Income.record(userId, eventId, amount, "Description", null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "ReceivedDate cannot be null"
    }

    def "should mark income as paid"() {
        given: "an income with PENDING status"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Description",
            LocalDate.now()
        )

        when: "marking it as paid"
        def updated = income.markAsPaid()

        then: "the status should be PAID and ID should remain the same"
        updated.id() == income.id()
        updated.status() == PaymentStatus.PAID
    }

    def "should mark income as overdue"() {
        given: "an income with PENDING status"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Description",
            LocalDate.now()
        )

        when: "marking it as overdue"
        def updated = income.markAsOverdue()

        then: "the status should be OVERDUE"
        updated.status() == PaymentStatus.OVERDUE
    }

    def "should cancel income"() {
        given: "an income with PENDING status"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Description",
            LocalDate.now()
        )

        when: "cancelling it"
        def updated = income.cancel()

        then: "the status should be CANCELLED"
        updated.status() == PaymentStatus.CANCELLED
    }

    def "should update income details"() {
        given: "an existing income"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Original description",
            LocalDate.of(2026, 1, 15)
        )
        def newAmount = Money.gbp("750.00")
        def newDescription = "Updated description"
        def newDate = LocalDate.of(2026, 2, 20)

        when: "updating the income"
        def updated = income.update(newAmount, newDescription, newDate)

        then: "the income should have updated values but same ID and status"
        updated.id() == income.id()
        updated.amount() == newAmount
        updated.description() == newDescription
        updated.receivedDate() == newDate
        updated.status() == income.status()
    }

    def "should be equal when IDs are equal"() {
        given: "the same income ID and different details"
        def incomeId = IncomeId.generate()
        def income1 = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Description 1",
            LocalDate.now()
        )
        def income2 = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("1000.00"),
            "Description 2",
            LocalDate.now()
        )

        expect: "they should be equal (entity equality is based on ID)"
        income1 == income2
        income1.hashCode() == income2.hashCode()
    }

    def "should not be equal when IDs are different"() {
        given: "different income IDs but same details"
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Same description"
        def date = LocalDate.now()

        when: "creating two incomes with different IDs"
        def income1 = Income.record(userId, eventId, amount, description, date)
        def income2 = Income.record(userId, eventId, amount, description, date)

        then: "they should not be equal"
        income1 != income2
    }

    def "should have meaningful toString"() {
        given: "an income"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Website design",
            LocalDate.of(2026, 1, 15)
        )

        when: "converting to string"
        def result = income.toString()

        then: "it should contain key information"
        result.contains(income.id().toString())
        result.contains("500.00")
        result.contains("PENDING")
    }
}
