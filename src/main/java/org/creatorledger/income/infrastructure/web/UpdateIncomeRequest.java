package org.creatorledger.income.infrastructure.web;

import java.time.LocalDate;

public record UpdateIncomeRequest(
        String amount,
        String currency,
        String description,
        LocalDate receivedDate
) {
    public UpdateIncomeRequest {
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
}
