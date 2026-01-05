package org.creatorledger.event.domain;

import java.time.LocalDate;

public record EventDate(LocalDate value) {

    private static final int MAX_YEARS_IN_PAST = 10;
    private static final int MAX_YEARS_IN_FUTURE = 5;

    public static EventDate of(final LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("EventDate cannot be null");
        }

        LocalDate now = LocalDate.now();
        LocalDate earliestValid = now.minusYears(MAX_YEARS_IN_PAST);
        LocalDate latestValid = now.plusYears(MAX_YEARS_IN_FUTURE);

        if (value.isBefore(earliestValid)) {
            throw new IllegalArgumentException(
                "EventDate cannot be more than " + MAX_YEARS_IN_PAST + " years in the past"
            );
        }

        if (value.isAfter(latestValid)) {
            throw new IllegalArgumentException(
                "EventDate cannot be more than " + MAX_YEARS_IN_FUTURE + " years in the future"
            );
        }

        return new EventDate(value);
    }

    public static EventDate today() {
        return new EventDate(LocalDate.now());
    }

    public boolean isBefore(EventDate other) {
        return value.isBefore(other.value);
    }

    public boolean isAfter(EventDate other) {
        return value.isAfter(other.value);
    }

    @Override
    public String toString() {
        return "EventDate[" + value + "]";
    }
}
