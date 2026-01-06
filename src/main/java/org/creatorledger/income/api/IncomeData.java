package org.creatorledger.income.api;

import org.creatorledger.common.Money;
import org.creatorledger.event.api.EventId;
import org.creatorledger.income.domain.Income;
import org.creatorledger.income.api.PaymentStatus;
import org.creatorledger.user.api.UserId;

import java.time.LocalDate;

/**
 * Data transfer object for Income across module boundaries.
 * <p>
 * This is part of the income module's published API, allowing
 * other modules to consume income data without depending on
 * internal domain types.
 * </p>
 */
public record IncomeData(
    IncomeId id,
    UserId userId,
    EventId eventId,
    Money amount,
    String description,
    LocalDate receivedDate,
    PaymentStatus status
) {

    public static IncomeData from(final Income income) {
        return new IncomeData(
            income.id(),
            income.userId(),
            income.eventId(),
            income.amount(),
            income.description(),
            income.receivedDate(),
            income.status()
        );
    }
}
