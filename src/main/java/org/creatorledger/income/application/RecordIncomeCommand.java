package org.creatorledger.income.application;

import org.creatorledger.event.api.EventId;
import org.creatorledger.user.api.UserId;

import java.time.LocalDate;

public record RecordIncomeCommand(
        UserId userId,
        EventId eventId,
        String amount,
        String currency,
        String description,
        LocalDate receivedDate
) {

    public RecordIncomeCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (amount == null || amount.isBlank()) {
            throw new IllegalArgumentException("Amount cannot be null or blank");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (receivedDate == null) {
            throw new IllegalArgumentException("Received date cannot be null");
        }
    }

    @Override
    public String toString() {
        return "RecordIncomeCommand[userId=%s, eventId=%s, amount=%s, currency=%s, description=%s, receivedDate=%s]"
                .formatted(userId, eventId, amount, currency, description, receivedDate);
    }
}
