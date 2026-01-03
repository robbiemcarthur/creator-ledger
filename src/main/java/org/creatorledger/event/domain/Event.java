package org.creatorledger.event.domain;
import org.creatorledger.common.EventId;

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

    /**
     * Creates a new event with a generated ID.
     *
     * @param date the event date
     * @param clientName the client name
     * @param description the event description
     * @return a new Event
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static Event create(EventDate date, ClientName clientName, String description) {
        return create(EventId.generate(), date, clientName, description);
    }

    /**
     * Creates a new event with a specific ID.
     *
     * @param id the event ID
     * @param date the event date
     * @param clientName the client name
     * @param description the event description
     * @return a new Event
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static Event create(EventId id, EventDate date, ClientName clientName, String description) {
        validateEventId(id);
        validateEventDate(date);
        validateClientName(clientName);
        validateDescription(description);
        return new Event(id, date, clientName, description.trim());
    }

    /**
     * Updates the event details, returning a new Event with the same ID.
     *
     * @param date the new event date
     * @param clientName the new client name
     * @param description the new event description
     * @return a new Event with updated details
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Event update(EventDate date, ClientName clientName, String description) {
        validateEventDate(date);
        validateClientName(clientName);
        validateDescription(description);
        return new Event(this.id, date, clientName, description.trim());
    }

    private static void validateEventId(EventId id) {
        if (id == null) {
            throw new IllegalArgumentException("EventId cannot be null");
        }
    }

    private static void validateEventDate(EventDate date) {
        if (date == null) {
            throw new IllegalArgumentException("EventDate cannot be null");
        }
    }

    private static void validateClientName(ClientName clientName) {
        if (clientName == null) {
            throw new IllegalArgumentException("ClientName cannot be null");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Event other)) return false;
        // Entity equality is based on ID only
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        // Entity hashCode is based on ID only
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Event[id=" + id + ", date=" + date.value() +
               ", clientName=" + clientName.value() + ", description=" + description + "]";
    }
}
