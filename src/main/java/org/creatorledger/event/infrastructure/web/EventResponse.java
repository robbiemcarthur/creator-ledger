package org.creatorledger.event.infrastructure.web;

import org.creatorledger.event.domain.Event;

/**
 * Response DTO for event data in API responses.
 * <p>
 * This is a presentation layer concern for HTTP API communication.
 * It represents event data in a format suitable for API consumers.
 * </p>
 *
 * @param id the event's unique identifier
 * @param date the event date (ISO-8601 format)
 * @param clientName the client name
 * @param description the event description
 */
public record EventResponse(String id, String date, String clientName, String description) {

    public static EventResponse from(final Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        return new EventResponse(
                event.id().value().toString(),
                event.date().value().toString(),
                event.clientName().value(),
                event.description()
        );
    }
}
