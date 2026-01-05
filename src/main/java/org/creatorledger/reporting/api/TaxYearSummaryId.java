package org.creatorledger.reporting.api;

import java.util.UUID;

public record TaxYearSummaryId(UUID value) {

    public static TaxYearSummaryId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Tax year summary ID cannot be null");
        }
        return new TaxYearSummaryId(value);
    }

    public static TaxYearSummaryId generate() {
        return new TaxYearSummaryId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "TaxYearSummaryId[%s]".formatted(value);
    }
}
