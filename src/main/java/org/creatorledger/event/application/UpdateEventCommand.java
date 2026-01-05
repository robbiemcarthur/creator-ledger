package org.creatorledger.event.application;

import org.creatorledger.event.api.EventId;

import java.time.LocalDate;

public record UpdateEventCommand(EventId eventId, LocalDate date, String clientName, String description) {

    public UpdateEventCommand {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
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

    @Override
    public String toString() {
        return "UpdateEventCommand[eventId=%s, date=%s, clientName=%s, description=%s]"
            .formatted(eventId, date, clientName, description);
    }
}
