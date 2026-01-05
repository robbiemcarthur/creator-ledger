package org.creatorledger.event.infrastructure.web;

import java.time.LocalDate;

/**
 * Request DTO for event creation endpoint.
 * <p>
 * This is a presentation layer concern for HTTP API communication.
 * It is converted to CreateEventCommand for application layer processing.
 * </p>
 *
 * @param date the event date
 * @param clientName the client name
 * @param description the event description
 */
public record CreateEventRequest(LocalDate date, String clientName, String description) {

    public CreateEventRequest {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Client name cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
    }
}
