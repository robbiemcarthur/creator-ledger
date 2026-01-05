package org.creatorledger.income.application;

import org.creatorledger.income.api.IncomeId;

import java.time.LocalDate;

public record UpdateIncomeCommand(
        IncomeId incomeId,
        String amount,
        String currency,
        String description,
        LocalDate receivedDate
) {

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
