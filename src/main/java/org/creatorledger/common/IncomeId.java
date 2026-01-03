package org.creatorledger.common;

import java.util.UUID;

/**
 * Value object representing a unique income identifier.
 * Wraps a UUID to provide type safety and domain meaning.
 */
public record IncomeId(UUID value) {

    /**
     * Creates an IncomeId from an existing UUID.
     *
     * @param value the UUID value
     * @return a new IncomeId
     * @throws IllegalArgumentException if value is null
     */
    public static IncomeId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("IncomeId cannot be null");
        }
        return new IncomeId(value);
    }

    /**
     * Generates a new IncomeId with a random UUID.
     *
     * @return a new IncomeId with a generated UUID
     */
    public static IncomeId generate() {
        return new IncomeId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "IncomeId[" + value + "]";
    }
}
