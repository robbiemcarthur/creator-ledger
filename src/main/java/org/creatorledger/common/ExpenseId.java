package org.creatorledger.common;

import java.util.UUID;

public record ExpenseId(UUID value) {
    public static ExpenseId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ExpenseId cannot be null");
        }
        return new ExpenseId(value);
    }

    public static ExpenseId generate() {
        return new ExpenseId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "ExpenseId[" + value + "]";
    }
}
