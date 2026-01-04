package org.creatorledger.income.infrastructure;

import org.creatorledger.common.Money;
import org.creatorledger.event.api.EventId;
import org.creatorledger.income.api.IncomeId;
import org.creatorledger.income.domain.Income;
import org.creatorledger.income.domain.PaymentStatus;
import org.creatorledger.user.api.UserId;

/**
 * Mapper for converting between domain Income and IncomeJpaEntity.
 * <p>
 * This keeps the domain model clean from persistence annotations and
 * allows the infrastructure to evolve independently from the domain.
 * </p>
 */
public class IncomeEntityMapper {

    /**
     * Converts a domain Income to a JPA entity.
     *
     * @param income the domain income
     * @return the JPA entity
     */
    public static IncomeJpaEntity toEntity(Income income) {
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

    /**
     * Converts a JPA entity to a domain Income.
     *
     * @param entity the JPA entity
     * @return the domain income
     */
    public static Income toDomain(IncomeJpaEntity entity) {
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

        // Apply the status from the entity (could be different from PENDING)
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
