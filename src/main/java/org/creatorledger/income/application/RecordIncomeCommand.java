package org.creatorledger.income.application;

import org.creatorledger.event.api.EventId;
import org.creatorledger.user.api.UserId;

import java.time.LocalDate;

/**
 * Command to record new income.
 * <p>
 * This command is used by the application layer to encapsulate the intent
 * to record new income in the system.
 * </p>
 *
 * @param userId the user ID
 * @param eventId the associated event ID
 * @param amount the income amount as a string
 * @param currency the currency code (e.g., "GBP", "USD")
 * @param description the income description
 * @param receivedDate the date income was received
 */
public record RecordIncomeCommand(
        UserId userId,
        EventId eventId,
        String amount,
        String currency,
        String description,
        LocalDate receivedDate
) {

    /**
     * Creates a new RecordIncomeCommand.
     *
     * @throws IllegalArgumentException if any field is null or invalid
     */
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
