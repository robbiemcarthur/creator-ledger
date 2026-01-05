package org.creatorledger.event.infrastructure.web;

import java.time.LocalDate;

/**
 * Request DTO for event update endpoint.
 * <p>
 * This is a presentation layer concern for HTTP API communication.
 * It is converted to UpdateEventCommand for application layer processing.
 * </p>
 *
 * @param date the new event date
 * @param clientName the new client name
 * @param description the new event description
 */
public record UpdateEventRequest(LocalDate date, String clientName, String description) {

    public UpdateEventRequest {
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
