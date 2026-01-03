package org.creatorledger.common;

import java.util.UUID;

/**
 * Value object representing a unique user identifier.
 * Wraps a UUID to provide type safety and domain meaning.
 */
public record UserId(UUID value) {

    /**
     * Creates a UserId from an existing UUID.
     *
     * @param value the UUID value
     * @return a new UserId
     * @throws IllegalArgumentException if value is null
     */
    public static UserId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new UserId(value);
    }

    /**
     * Generates a new UserId with a random UUID.
     *
     * @return a new UserId with a generated UUID
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "UserId[" + value + "]";
    }
}
