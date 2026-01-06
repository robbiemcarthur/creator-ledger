package org.creatorledger.income.domain;

import org.creatorledger.event.api.EventId;
import org.creatorledger.income.api.IncomeId;
import org.creatorledger.income.api.PaymentStatus;
import org.creatorledger.common.Money;
import org.creatorledger.user.api.UserId;

import java.time.LocalDate;

public record Income(
    IncomeId id,
    UserId userId,
    EventId eventId,
    Money amount,
    String description,
    LocalDate receivedDate,
    PaymentStatus status
) {

    public static Income record(UserId userId, EventId eventId, Money amount, String description, LocalDate receivedDate) {
        return record(IncomeId.generate(), userId, eventId, amount, description, receivedDate);
    }

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
    public Income update(final Money amount, final String description, final LocalDate receivedDate) {
        validateAmount(amount);
        validateDescription(description);
        validateReceivedDate(receivedDate);

        return new Income(this.id, this.userId, this.eventId, amount, description.trim(), receivedDate, this.status);
    }

    private static void validateIncomeId(final IncomeId id) {
        if (id == null) {
            throw new IllegalArgumentException("IncomeId cannot be null");
        }
    }

    private static void validateUserId(final UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    private static void validateEventId(final EventId eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("EventId cannot be null");
        }
    }

    private static void validateAmount(final Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
    }

    private static void validateDescription(final String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
    }

    private static void validateReceivedDate(final LocalDate receivedDate) {
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
