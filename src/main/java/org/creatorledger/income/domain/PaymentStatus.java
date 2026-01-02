package org.creatorledger.income.domain;

/**
 * Enum representing the payment status of income.
 * Tracks whether income has been paid, is pending, overdue, or cancelled.
 */
public enum PaymentStatus {
    /** Payment is pending */
    PENDING,

    /** Payment has been received */
    PAID,

    /** Payment is overdue */
    OVERDUE,

    /** Payment has been cancelled */
    CANCELLED;

    /**
     * Checks if this status represents paid income.
     *
     * @return true if status is PAID
     */
    public boolean isPaid() {
        return this == PAID;
    }
}
