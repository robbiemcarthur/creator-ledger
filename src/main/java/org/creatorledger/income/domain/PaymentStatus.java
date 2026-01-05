package org.creatorledger.income.domain;

/**
 * Enum representing the payment status of income.
 * Tracks whether income has been paid, is pending, overdue, or cancelled.
 */
public enum PaymentStatus {
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED;
    public boolean isPaid() {
        return this == PAID;
    }
}
