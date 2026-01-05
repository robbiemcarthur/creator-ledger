package org.creatorledger.income.api;

import java.util.UUID;

public record IncomeId(UUID value) {

    public static IncomeId of(final UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("IncomeId cannot be null");
        }
        return new IncomeId(value);
    }

    public static IncomeId generate() {
        return new IncomeId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "IncomeId[" + value + "]";
    }
}
