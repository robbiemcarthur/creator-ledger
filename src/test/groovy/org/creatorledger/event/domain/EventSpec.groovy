package org.creatorledger.event.domain

import org.creatorledger.common.EventId
import org.creatorledger.common.UserId
import spock.lang.Specification
import java.time.LocalDate

class EventSpec extends Specification {

    def "should create a new event with generated ID"() {
        given: "valid event details"
        def date = EventDate.of(LocalDate.of(2026, 2, 15))
        def clientName = ClientName.of("Acme Corp")
        def description = "Annual company party"

        when: "creating a new event"
        def event = Event.create(date, clientName, description)

        then: "the event should have all the data"
        event.id() != null
        event.date() == date
        event.clientName() == clientName
        event.description() == description
    }

    def "should create a new event with specific ID"() {
        given: "valid event details with specific ID"
        def eventId = EventId.generate()
        def date = EventDate.of(LocalDate.of(2026, 2, 15))
        def clientName = ClientName.of("Acme Corp")
        def description = "Annual company party"

        when: "creating a new event with specific ID"
        def event = Event.create(eventId, date, clientName, description)

        then: "the event should have the specified ID"
        event.id() == eventId
        event.date() == date
        event.clientName() == clientName
        event.description() == description
    }

    def "should reject null date when creating"() {
        given: "a null date"
        def clientName = ClientName.of("Acme Corp")
        def description = "Test event"

        when: "creating an event with null date"
        Event.create(null, clientName, description)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "EventDate cannot be null"
    }

    def "should reject null client name when creating"() {
        given: "a null client name"
        def date = EventDate.today()
        def description = "Test event"

        when: "creating an event with null client name"
        Event.create(date, null, description)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "ClientName cannot be null"
    }

    def "should reject null description when creating"() {
        given: "a null description"
        def date = EventDate.today()
        def clientName = ClientName.of("Acme Corp")

        when: "creating an event with null description"
        Event.create(date, clientName, null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Description cannot be null or blank"
    }

    def "should reject blank description when creating"() {
        given: "a blank description"
        def date = EventDate.today()
        def clientName = ClientName.of("Acme Corp")

        when: "creating an event with blank description"
        Event.create(date, clientName, "   ")

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Description cannot be null or blank"
    }

    def "should update event details"() {
        given: "an existing event"
        def event = Event.create(
            EventDate.of(LocalDate.of(2026, 2, 15)),
            ClientName.of("Acme Corp"),
            "Original description"
        )
        def newDate = EventDate.of(LocalDate.of(2026, 3, 20))
        def newClientName = ClientName.of("New Client Ltd")
        def newDescription = "Updated description"

        when: "updating the event"
        def updated = event.update(newDate, newClientName, newDescription)

        then: "the event should have updated values but same ID"
        updated.id() == event.id()  // ID should not change
        updated.date() == newDate
        updated.clientName() == newClientName
        updated.description() == newDescription
    }

    def "should be equal when IDs are equal"() {
        given: "the same event ID and different details"
        def eventId = EventId.generate()
        def event1 = Event.create(
            eventId,
            EventDate.of(LocalDate.of(2026, 2, 15)),
            ClientName.of("Acme Corp"),
            "Description 1"
        )
        def event2 = Event.create(
            eventId,
            EventDate.of(LocalDate.of(2026, 3, 20)),
            ClientName.of("Different Client"),
            "Description 2"
        )

        expect: "they should be equal (entity equality is based on ID)"
        event1 == event2
        event1.hashCode() == event2.hashCode()
    }

    def "should not be equal when IDs are different"() {
        given: "different event IDs but same details"
        def date = EventDate.of(LocalDate.of(2026, 2, 15))
        def clientName = ClientName.of("Acme Corp")
        def description = "Same description"

        when: "creating two events with different IDs"
        def event1 = Event.create(date, clientName, description)
        def event2 = Event.create(date, clientName, description)

        then: "they should not be equal"
        event1 != event2
    }

    def "should have meaningful toString"() {
        given: "an event"
        def event = Event.create(
            EventDate.of(LocalDate.of(2026, 2, 15)),
            ClientName.of("Acme Corp"),
            "Company party"
        )

        when: "converting to string"
        def result = event.toString()

        then: "it should contain key information"
        result.contains(event.id().toString())
        result.contains("Acme Corp")
        result.contains("2026-02-15")
    }
}
