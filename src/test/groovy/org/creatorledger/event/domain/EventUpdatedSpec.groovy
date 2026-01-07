package org.creatorledger.event.domain

import org.creatorledger.event.api.EventId
import spock.lang.Specification
import java.time.Instant
import java.time.LocalDate

class EventUpdatedSpec extends Specification {

    def "should create EventUpdated event"() {
        given: "event details"
        def eventId = EventId.generate()
        def date = EventDate.of(LocalDate.of(2026, 6, 15))
        def clientName = ClientName.of("Updated Client")
        def description = "Updated event description"
        def occurredAt = Instant.now()

        when: "creating an EventUpdated event"
        def event = new EventUpdated(eventId, date, clientName, description, occurredAt)

        then: "it should contain all the data"
        event.eventId() == eventId
        event.date() == date
        event.clientName() == clientName
        event.description() == description
        event.occurredAt() == occurredAt
    }

    def "should create EventUpdated event with current timestamp"() {
        given: "event details"
        def eventId = EventId.generate()
        def date = EventDate.of(LocalDate.of(2026, 6, 15))
        def clientName = ClientName.of("Updated Client")
        def description = "Updated event description"
        def before = Instant.now()

        when: "creating an EventUpdated event without timestamp"
        def event = EventUpdated.of(eventId, date, clientName, description)
        def after = Instant.now()

        then: "it should have a timestamp between before and after"
        event.eventId() == eventId
        event.date() == date
        event.clientName() == clientName
        event.description() == description
        !event.occurredAt().isBefore(before)
        !event.occurredAt().isAfter(after)
    }

    def "should be equal when all fields are equal"() {
        given: "the same event data"
        def eventId = EventId.generate()
        def date = EventDate.of(LocalDate.of(2026, 6, 15))
        def clientName = ClientName.of("Updated Client")
        def description = "Updated event description"
        def occurredAt = Instant.now()

        when: "creating two events with same data"
        def event1 = new EventUpdated(eventId, date, clientName, description, occurredAt)
        def event2 = new EventUpdated(eventId, date, clientName, description, occurredAt)

        then: "they should be equal"
        event1 == event2
        event1.hashCode() == event2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "an EventUpdated event"
        def eventId = EventId.generate()
        def date = EventDate.of(LocalDate.of(2026, 6, 15))
        def clientName = ClientName.of("Updated Client")
        def description = "Updated event description"
        def event = EventUpdated.of(eventId, date, clientName, description)

        when: "converting to string"
        def result = event.toString()

        then: "it should contain key information"
        result.contains(eventId.toString())
        result.contains("Updated Client")
        result.contains("2026-06-15")
    }
}
