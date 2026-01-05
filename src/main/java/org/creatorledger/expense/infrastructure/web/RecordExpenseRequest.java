package org.creatorledger.expense.infrastructure.web;

import org.creatorledger.expense.api.ExpenseCategory;

import java.time.LocalDate;
import java.util.UUID;

public record RecordExpenseRequest(
        UUID userId,
        String amount,
        String currency,
        ExpenseCategory category,
        String description,
        LocalDate incurredDate
) {

    public RecordExpenseRequest {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (amount == null || amount.isBlank()) {
            throw new IllegalArgumentException("Amount cannot be null or blank");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (incurredDate == null) {
            throw new IllegalArgumentException("Incurred date cannot be null");
        }
    }
}
