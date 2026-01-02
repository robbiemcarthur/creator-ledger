package org.creatorledger.income.domain;

import org.creatorledger.event.domain.EventId;
import org.creatorledger.user.domain.UserId;

import java.time.LocalDate;

/**
 * Aggregate root representing income from a business event.
 * Income is identified by IncomeId and tracks payment status, amount, and associated event.
 */
public record Income(
    IncomeId id,
    UserId userId,
    EventId eventId,
    Money amount,
    String description,
    LocalDate receivedDate,
    PaymentStatus status
) {

    /**
     * Records new income with a generated ID.
     *
     * @param userId the user ID
     * @param eventId the associated event ID
     * @param amount the income amount
     * @param description the income description
     * @param receivedDate the date income was received
     * @return a new Income
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static Income record(UserId userId, EventId eventId, Money amount, String description, LocalDate receivedDate) {
        return record(IncomeId.generate(), userId, eventId, amount, description, receivedDate);
    }

    /**
     * Records new income with a specific ID.
     *
     * @param id the income ID
     * @param userId the user ID
     * @param eventId the associated event ID
     * @param amount the income amount
     * @param description the income description
     * @param receivedDate the date income was received
     * @return a new Income
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static Income record(IncomeId id, UserId userId, EventId eventId, Money amount, String description, LocalDate receivedDate) {
        validateIncomeId(id);
        validateUserId(userId);
        validateEventId(eventId);
        validateAmount(amount);
        validateDescription(description);
        validateReceivedDate(receivedDate);

        return new Income(id, userId, eventId, amount, description.trim(), receivedDate, PaymentStatus.PENDING);
    }

    /**
     * Marks this income as paid, returning a new Income with PAID status.
     *
     * @return a new Income with PAID status
     */
    public Income markAsPaid() {
        return new Income(id, userId, eventId, amount, description, receivedDate, PaymentStatus.PAID);
    }

    /**
     * Marks this income as overdue, returning a new Income with OVERDUE status.
     *
     * @return a new Income with OVERDUE status
     */
    public Income markAsOverdue() {
        return new Income(id, userId, eventId, amount, description, receivedDate, PaymentStatus.OVERDUE);
    }

    /**
     * Cancels this income, returning a new Income with CANCELLED status.
     *
     * @return a new Income with CANCELLED status
     */
    public Income cancel() {
        return new Income(id, userId, eventId, amount, description, receivedDate, PaymentStatus.CANCELLED);
    }

    /**
     * Updates the income details, returning a new Income with the same ID and status.
     *
     * @param amount the new amount
     * @param description the new description
     * @param receivedDate the new received date
     * @return a new Income with updated details
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Income update(Money amount, String description, LocalDate receivedDate) {
        validateAmount(amount);
        validateDescription(description);
        validateReceivedDate(receivedDate);

        return new Income(this.id, this.userId, this.eventId, amount, description.trim(), receivedDate, this.status);
    }

    private static void validateIncomeId(IncomeId id) {
        if (id == null) {
            throw new IllegalArgumentException("IncomeId cannot be null");
        }
    }

    private static void validateUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    private static void validateEventId(EventId eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("EventId cannot be null");
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
    }

    private static void validateReceivedDate(LocalDate receivedDate) {
        if (receivedDate == null) {
            throw new IllegalArgumentException("ReceivedDate cannot be null");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Income other)) return false;
        // Entity equality is based on ID only
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        // Entity hashCode is based on ID only
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Income[id=" + id + ", amount=" + amount + ", status=" + status +
               ", description=" + description + ", receivedDate=" + receivedDate + "]";
    }
}
