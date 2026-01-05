package org.creatorledger.income.domain;

import org.creatorledger.common.Money;
import org.creatorledger.income.api.IncomeId;
import org.creatorledger.event.api.EventId;
import org.creatorledger.user.api.UserId;

import java.time.Instant;
import java.time.LocalDate;

public record IncomeRecorded(
    IncomeId incomeId,
    UserId userId,
    EventId eventId,
    Money amount,
    String description,
    LocalDate receivedDate,
    Instant occurredAt
) {
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
