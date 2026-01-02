package org.creatorledger.income.domain

import org.creatorledger.event.domain.EventId
import org.creatorledger.user.domain.UserId
import spock.lang.Specification
import java.time.Instant
import java.time.LocalDate

class IncomeRecordedSpec extends Specification {

    def "should create IncomeRecorded event"() {
        given: "income details"
        def incomeId = IncomeId.generate()
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)
        def occurredAt = Instant.now()

        when: "creating an IncomeRecorded event"
        def event = new IncomeRecorded(incomeId, userId, eventId, amount, description, receivedDate, occurredAt)

        then: "it should contain all the data"
        event.incomeId() == incomeId
        event.userId() == userId
        event.eventId() == eventId
        event.amount() == amount
        event.description() == description
        event.receivedDate() == receivedDate
        event.occurredAt() == occurredAt
    }

    def "should create IncomeRecorded event with current timestamp"() {
        given: "income details"
        def incomeId = IncomeId.generate()
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)
        def before = Instant.now()

        when: "creating an IncomeRecorded event without timestamp"
        def event = IncomeRecorded.of(incomeId, userId, eventId, amount, description, receivedDate)
        def after = Instant.now()

        then: "it should have a timestamp between before and after"
        event.incomeId() == incomeId
        event.userId() == userId
        event.eventId() == eventId
        event.amount() == amount
        event.description() == description
        event.receivedDate() == receivedDate
        !event.occurredAt().isBefore(before)
        !event.occurredAt().isAfter(after)
    }

    def "should be equal when all fields are equal"() {
        given: "the same income data"
        def incomeId = IncomeId.generate()
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)
        def occurredAt = Instant.now()

        when: "creating two events with same data"
        def event1 = new IncomeRecorded(incomeId, userId, eventId, amount, description, receivedDate, occurredAt)
        def event2 = new IncomeRecorded(incomeId, userId, eventId, amount, description, receivedDate, occurredAt)

        then: "they should be equal"
        event1 == event2
        event1.hashCode() == event2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "an IncomeRecorded event"
        def incomeId = IncomeId.generate()
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)
        def event = IncomeRecorded.of(incomeId, userId, eventId, amount, description, receivedDate)

        when: "converting to string"
        def result = event.toString()

        then: "it should contain key information"
        result.contains(incomeId.toString())
        result.contains("500.00")
        result.contains("GBP")
    }
}
