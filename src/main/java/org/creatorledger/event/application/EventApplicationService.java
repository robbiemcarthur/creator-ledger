package org.creatorledger.event.application;

import org.creatorledger.event.api.EventId;
import org.creatorledger.event.domain.ClientName;
import org.creatorledger.event.domain.Event;
import org.creatorledger.event.domain.EventDate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service for event-related use cases.
 * <p>
 * This service orchestrates domain operations and coordinates with the repository
 * to persist changes. It serves as the entry point for event-related commands
 * and queries from the presentation layer.
 * </p>
 */
@Service
public class EventApplicationService {

    private final EventRepository eventRepository;

    public EventApplicationService(EventRepository eventRepository) {
        if (eventRepository == null) {
            throw new IllegalArgumentException("Event repository cannot be null");
        }
        this.eventRepository = eventRepository;
    }

    public EventId create(final CreateEventCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        final EventDate date = EventDate.of(command.date());
        final ClientName clientName = ClientName.of(command.clientName());
        final Event event = Event.create(date, clientName, command.description());

        eventRepository.save(event);

        return event.id();
    }

    public void update(final UpdateEventCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        final Event existingEvent = eventRepository.findById(command.eventId())
            .orElseThrow(() -> new IllegalStateException("Event not found: " + command.eventId()));

        final EventDate date = EventDate.of(command.date());
        final ClientName clientName = ClientName.of(command.clientName());
        final Event updatedEvent = existingEvent.update(date, clientName, command.description());

        eventRepository.save(updatedEvent);
    }

    public Optional<Event> findById(final EventId eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        return eventRepository.findById(eventId);
    }

    public boolean existsById(final EventId eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        return eventRepository.existsById(eventId);
    }
}
