package org.creatorledger.event.domain;
import org.creatorledger.event.api.EventId;

/**
 * Aggregate root representing a business event (gig, project, client work).
 * Events are identified by their EventId and have a date, client, and description.
 */
public record Event(
    EventId id,
    EventDate date,
    ClientName clientName,
    String description
) {
    public static Event create(final EventDate date, final ClientName clientName, final String description) {
        return create(EventId.generate(), date, clientName, description);
    }

    public static Event create(final EventId id, final EventDate date, final ClientName clientName, final String description) {
        validateEventId(id);
        validateEventDate(date);
        validateClientName(clientName);
        validateDescription(description);
        return new Event(id, date, clientName, description.trim());
    }

    public Event update(final EventDate date, final ClientName clientName, final String description) {
        validateEventDate(date);
        validateClientName(clientName);
        validateDescription(description);
        return new Event(this.id, date, clientName, description.trim());
    }

    private static void validateEventId(final EventId id) {
        if (id == null) {
            throw new IllegalArgumentException("EventId cannot be null");
        }
    }

    private static void validateEventDate(final EventDate date) {
        if (date == null) {
            throw new IllegalArgumentException("EventDate cannot be null");
        }
    }

    private static void validateClientName(final ClientName clientName) {
        if (clientName == null) {
            throw new IllegalArgumentException("ClientName cannot be null");
        }
    }

    private static void validateDescription(final String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Event other)) return false;

        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Event[id=" + id + ", date=" + date.value() +
               ", clientName=" + clientName.value() + ", description=" + description + "]";
    }
}
