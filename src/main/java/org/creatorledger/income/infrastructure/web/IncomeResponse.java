package org.creatorledger.income.infrastructure.web;

import org.creatorledger.income.domain.Income;

public record IncomeResponse(
        String id,
        String userId,
        String eventId,
        String amount,
        String currency,
        String description,
        String receivedDate,
        String status
) {

    public static IncomeResponse from(Income income) {
        if (income == null) {
            throw new IllegalArgumentException("Income cannot be null");
        }
        return new IncomeResponse(
                income.id().value().toString(),
                income.userId().value().toString(),
                income.eventId().value().toString(),
                income.amount().amount().toString(),
                income.amount().currency(),
                income.description(),
                income.receivedDate().toString(),
                income.status().name()
        );
    }
}
