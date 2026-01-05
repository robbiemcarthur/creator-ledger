package org.creatorledger.income.infrastructure;

import org.creatorledger.common.Money;
import org.creatorledger.event.api.EventId;
import org.creatorledger.income.api.IncomeId;
import org.creatorledger.income.domain.Income;
import org.creatorledger.income.domain.PaymentStatus;
import org.creatorledger.user.api.UserId;

public class IncomeEntityMapper {

    public static IncomeJpaEntity toEntity(final Income income) {
        if (income == null) {
            return null;
        }
        return new IncomeJpaEntity(
                income.id().value(),
                income.userId().value(),
                income.eventId().value(),
                income.amount().amount(),
                income.amount().currency(),
                income.description(),
                income.receivedDate(),
                income.status().name()
        );
    }

    public static Income toDomain(final IncomeJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Money amount = Money.of(entity.getAmount(), entity.getCurrency());
        Income income = Income.record(
                IncomeId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                EventId.of(entity.getEventId()),
                amount,
                entity.getDescription(),
                entity.getReceivedDate()
        );

        PaymentStatus status = PaymentStatus.valueOf(entity.getStatus());
        if (status == PaymentStatus.PAID) {
            return income.markAsPaid();
        } else if (status == PaymentStatus.OVERDUE) {
            return income.markAsOverdue();
        } else if (status == PaymentStatus.CANCELLED) {
            return income.cancel();
        }

        return income;
    }
}
