package org.creatorledger.event.domain;

import org.creatorledger.event.api.EventId;

import java.time.Instant;

/**
 * Domain event published when an event is created.
 * This event can be consumed by other modules for cross-cutting concerns.
 */
public record EventCreated(
    EventId eventId,
    EventDate date,
    ClientName clientName,
    String description,
    Instant occurredAt
) {

    public static EventCreated of(EventId eventId, EventDate date, ClientName clientName, String description) {
        return new EventCreated(eventId, date, clientName, description, Instant.now());
    }

    @Override
    public String toString() {
        return "EventCreated[eventId=" + eventId + ", date=" + date.value() +
               ", clientName=" + clientName.value() + ", description=" + description +
               ", occurredAt=" + occurredAt + "]";
    }
}
