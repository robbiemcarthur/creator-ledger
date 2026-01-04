package org.creatorledger.income.application;

import org.creatorledger.income.api.IncomeId;

import java.time.LocalDate;

/**
 * Command to update an existing income.
 * <p>
 * This command is used by the application layer to encapsulate the intent
 * to update an income's details.
 * </p>
 *
 * @param incomeId the ID of the income to update
 * @param amount the new income amount as a string
 * @param currency the currency code (e.g., "GBP", "USD")
 * @param description the new income description
 * @param receivedDate the new received date
 */
public record UpdateIncomeCommand(
        IncomeId incomeId,
        String amount,
        String currency,
        String description,
        LocalDate receivedDate
) {

    /**
     * Creates a new UpdateIncomeCommand.
     *
     * @throws IllegalArgumentException if any field is null or invalid
     */
    public UpdateIncomeCommand {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
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
        return "UpdateIncomeCommand[incomeId=%s, amount=%s, currency=%s, description=%s, receivedDate=%s]"
                .formatted(incomeId, amount, currency, description, receivedDate);
    }
}
