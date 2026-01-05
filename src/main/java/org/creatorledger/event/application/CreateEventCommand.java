package org.creatorledger.event.application;

import java.time.LocalDate;

public record CreateEventCommand(LocalDate date, String clientName, String description) {

    public CreateEventCommand {
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
        return "CreateEventCommand[date=%s, clientName=%s, description=%s]"
            .formatted(date, clientName, description);
    }
}
