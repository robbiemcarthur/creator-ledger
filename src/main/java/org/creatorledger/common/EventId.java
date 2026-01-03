package org.creatorledger.common;

import java.util.UUID;

/**
 * Value object representing a unique event identifier.
 * Wraps a UUID to provide type safety and domain meaning.
 */
public record EventId(UUID value) {

    /**
     * Creates an EventId from an existing UUID.
     *
     * @param value the UUID value
     * @return a new EventId
     * @throws IllegalArgumentException if value is null
     */
    public static EventId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("EventId cannot be null");
        }
        return new EventId(value);
    }

    /**
     * Generates a new EventId with a random UUID.
     *
     * @return a new EventId with a generated UUID
     */
    public static EventId generate() {
        return new EventId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "EventId[" + value + "]";
    }
}
