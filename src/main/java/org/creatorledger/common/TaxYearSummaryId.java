package org.creatorledger.common;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a tax year summary.
 * <p>
 * This ID is used to uniquely identify a tax year summary aggregate root.
 * It wraps a UUID to provide type safety and prevent mixing different ID types.
 * </p>
 *
 * @param value the UUID value
 */
public record TaxYearSummaryId(UUID value) {

    /**
     * Creates a tax year summary ID from a UUID.
     *
     * @param value the UUID value
     * @return a new TaxYearSummaryId instance
     * @throws IllegalArgumentException if value is null
     */
    public static TaxYearSummaryId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Tax year summary ID cannot be null");
        }
        return new TaxYearSummaryId(value);
    }

    /**
     * Generates a new random tax year summary ID.
     *
     * @return a new TaxYearSummaryId with a random UUID
     */
    public static TaxYearSummaryId generate() {
        return new TaxYearSummaryId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "TaxYearSummaryId[%s]".formatted(value);
    }
}
