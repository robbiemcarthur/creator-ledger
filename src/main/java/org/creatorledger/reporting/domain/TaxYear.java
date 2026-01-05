package org.creatorledger.reporting.domain;

import java.time.LocalDate;

public record TaxYear(Integer startYear) {

    private static final int APRIL = 4;
    private static final int TAX_YEAR_START_DAY = 6;
    private static final int TAX_YEAR_END_DAY = 5;
    private static final int MAX_YEARS_IN_PAST = 10;
    private static final int MAX_YEARS_IN_FUTURE = 5;

    public static TaxYear of(Integer startYear) {
        validateStartYear(startYear);
        return new TaxYear(startYear);
    }

    public LocalDate startDate() {
        return LocalDate.of(startYear, APRIL, TAX_YEAR_START_DAY);
    }

    public LocalDate endDate() {
        return LocalDate.of(startYear + 1, APRIL, TAX_YEAR_END_DAY);
    }

    public boolean contains(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(startDate()) && !date.isAfter(endDate());
    }

    @Override
    public String toString() {
        return "TaxYear[%d-%d]".formatted(startYear, startYear + 1);
    }

    private static void validateStartYear(Integer startYear) {
        if (startYear == null) {
            throw new IllegalArgumentException("Start year cannot be null");
        }

        int currentYear = LocalDate.now().getYear();
        int minYear = currentYear - MAX_YEARS_IN_PAST;
        int maxYear = currentYear + MAX_YEARS_IN_FUTURE;

        if (startYear < minYear || startYear > maxYear) {
            throw new IllegalArgumentException(
                    "Start year must be within %d years in the past and %d years in the future (got %d)"
                            .formatted(MAX_YEARS_IN_PAST, MAX_YEARS_IN_FUTURE, startYear)
            );
        }
    }
}
