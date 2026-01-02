package org.creatorledger.income.domain;

import org.creatorledger.event.domain.EventId;
import org.creatorledger.user.domain.UserId;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Domain event published when income is recorded.
 * This event can be consumed by other modules for cross-cutting concerns.
 */
public record IncomeRecorded(
    IncomeId incomeId,
    UserId userId,
    EventId eventId,
    Money amount,
    String description,
    LocalDate receivedDate,
    Instant occurredAt
) {

    /**
     * Creates an IncomeRecorded event with the current timestamp.
     *
     * @param incomeId the ID of the recorded income
     * @param userId the user ID
     * @param eventId the associated event ID
     * @param amount the income amount
     * @param description the income description
     * @param receivedDate the date income was received
     * @return a new IncomeRecorded event
     */
    public static IncomeRecorded of(IncomeId incomeId, UserId userId, EventId eventId, Money amount, String description, LocalDate receivedDate) {
        return new IncomeRecorded(incomeId, userId, eventId, amount, description, receivedDate, Instant.now());
    }

    @Override
    public String toString() {
        return "IncomeRecorded[incomeId=" + incomeId + ", userId=" + userId +
               ", eventId=" + eventId + ", amount=" + amount + ", description=" + description +
               ", receivedDate=" + receivedDate + ", occurredAt=" + occurredAt + "]";
    }
}
