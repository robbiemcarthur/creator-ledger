package org.creatorledger.event.application

import org.creatorledger.event.domain.ClientName
import org.creatorledger.event.domain.Event
import org.creatorledger.event.domain.EventCreated
import org.creatorledger.event.domain.EventDate
import org.creatorledger.event.domain.EventUpdated
import org.creatorledger.event.api.EventId
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification
import java.time.LocalDate

class EventApplicationServiceSpec extends Specification {

    EventRepository eventRepository
    ApplicationEventPublisher eventPublisher
    EventApplicationService service

    def setup() {
        eventRepository = Mock(EventRepository)
        eventPublisher = Mock(ApplicationEventPublisher)
        service = new EventApplicationService(eventRepository, eventPublisher)
    }

    def "should create a new event"() {
        given: "a create event command"
        def command = new CreateEventCommand(
            LocalDate.of(2026, 3, 15),
            "Acme Corporation",
            "Website redesign workshop"
        )

        when: "creating the event"
        def eventId = service.create(command)

        then: "the event is saved via repository"
        1 * eventRepository.save(_) >> { Event event ->
            assert event != null
            assert event.date() == EventDate.of(LocalDate.of(2026, 3, 15))
            assert event.clientName() == ClientName.of("Acme Corporation")
            assert event.description() == "Website redesign workshop"
            return event
        }

        and: "the event ID is returned"
        eventId != null
    }

    def "should publish EventCreated event when creating event"() {
        given: "a create event command"
        def command = new CreateEventCommand(
            LocalDate.of(2026, 3, 15),
            "Acme Corporation",
            "Website redesign workshop"
        )

        when: "creating the event"
        service.create(command)

        then: "the event is saved"
        1 * eventRepository.save(_) >> { Event event -> event }

        and: "EventCreated event is published"
        1 * eventPublisher.publishEvent(_) >> { arguments ->
            def event = arguments[0]
            assert event instanceof EventCreated
            assert event.clientName().value() == "Acme Corporation"
            assert event.description() == "Website redesign workshop"
            assert event.occurredAt() != null
        }
    }

    def "should reject null command when creating"() {
        when: "creating with null command"
        service.create(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should update an existing event"() {
        given: "an existing event"
        def eventId = EventId.generate()
        def existingEvent = Event.create(
            eventId,
            EventDate.of(LocalDate.of(2026, 3, 15)),
            ClientName.of("Acme Corporation"),
            "Original description"
        )

        and: "repository returns the existing event"
        eventRepository.findById(eventId) >> Optional.of(existingEvent)

        and: "an update command"
        def command = new UpdateEventCommand(
            eventId,
            LocalDate.of(2026, 4, 20),
            "Beta Industries",
            "Updated description"
        )

        when: "updating the event"
        service.update(command)

        then: "the event is saved with updated details"
        1 * eventRepository.save(_) >> { Event event ->
            assert event.id() == eventId
            assert event.date() == EventDate.of(LocalDate.of(2026, 4, 20))
            assert event.clientName() == ClientName.of("Beta Industries")
            assert event.description() == "Updated description"
            return event
        }
    }

    def "should publish EventUpdated event when updating event"() {
        given: "an existing event"
        def eventId = EventId.generate()
        def existingEvent = Event.create(
            eventId,
            EventDate.of(LocalDate.of(2026, 3, 15)),
            ClientName.of("Acme Corporation"),
            "Original description"
        )

        and: "repository returns the existing event"
        eventRepository.findById(eventId) >> Optional.of(existingEvent)

        and: "an update command"
        def command = new UpdateEventCommand(
            eventId,
            LocalDate.of(2026, 4, 20),
            "Beta Industries",
            "Updated description"
        )

        when: "updating the event"
        service.update(command)

        then: "the event is saved"
        1 * eventRepository.save(_) >> { Event event -> event }

        and: "EventUpdated event is published"
        1 * eventPublisher.publishEvent(_) >> { arguments ->
            def event = arguments[0]
            assert event instanceof EventUpdated
            assert event.eventId() == eventId
            assert event.clientName().value() == "Beta Industries"
            assert event.description() == "Updated description"
            assert event.occurredAt() != null
        }
    }

    def "should reject null command when updating"() {
        when: "updating with null command"
        service.update(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should throw exception when updating non-existent event"() {
        given: "a non-existent event ID"
        def eventId = EventId.generate()

        and: "repository returns empty"
        eventRepository.findById(eventId) >> Optional.empty()

        and: "an update command"
        def command = new UpdateEventCommand(
            eventId,
            LocalDate.of(2026, 4, 20),
            "Beta Industries",
            "Updated description"
        )

        when: "updating the event"
        service.update(command)

        then: "it throws IllegalStateException"
        def exception = thrown(IllegalStateException)
        exception.message == "Event not found: " + eventId
    }

    def "should find event by ID when exists"() {
        given: "an existing event ID"
        def eventId = EventId.generate()
        def existingEvent = Event.create(
            eventId,
            EventDate.of(LocalDate.of(2026, 3, 15)),
            ClientName.of("Acme Corporation"),
            "Website redesign workshop"
        )

        and: "repository returns the event"
        eventRepository.findById(eventId) >> Optional.of(existingEvent)

        when: "finding the event by ID"
        def result = service.findById(eventId)

        then: "the event is returned"
        result.isPresent()
        result.get() == existingEvent
    }

    def "should return empty when event not found"() {
        given: "a non-existent event ID"
        def eventId = EventId.generate()

        and: "repository returns empty"
        eventRepository.findById(eventId) >> Optional.empty()

        when: "finding the event by ID"
        def result = service.findById(eventId)

        then: "empty is returned"
        result.isEmpty()
    }

    def "should reject null event ID when finding"() {
        when: "finding with null ID"
        service.findById(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Event ID cannot be null"
    }

    def "should check if event exists"() {
        given: "an existing event ID"
        def eventId = EventId.generate()

        and: "repository confirms existence"
        eventRepository.existsById(eventId) >> true

        when: "checking if event exists"
        def exists = service.existsById(eventId)

        then: "true is returned"
        exists
    }

    def "should return false when event does not exist"() {
        given: "a non-existent event ID"
        def eventId = EventId.generate()

        and: "repository returns false"
        eventRepository.existsById(eventId) >> false

        when: "checking if event exists"
        def exists = service.existsById(eventId)

        then: "false is returned"
        !exists
    }

    def "should reject null event ID when checking existence"() {
        when: "checking existence with null ID"
        service.existsById(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Event ID cannot be null"
    }
}
