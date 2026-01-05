package org.creatorledger.expense.infrastructure;

import org.creatorledger.common.Money;
import org.creatorledger.expense.api.ExpenseCategory;
import org.creatorledger.expense.api.ExpenseId;
import org.creatorledger.expense.domain.Expense;
import org.creatorledger.user.api.UserId;

public class ExpenseEntityMapper {

    public static ExpenseJpaEntity toEntity(final Expense expense) {
        if (expense == null) {
            return null;
        }
        return new ExpenseJpaEntity(
                expense.id().value(),
                expense.userId().value(),
                expense.amount().amount(),
                expense.amount().currency(),
                expense.category().name(),
                expense.description(),
                expense.incurredDate()
        );
    }

    public static Expense toDomain(final ExpenseJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        final Money amount = Money.of(entity.getAmount(), entity.getCurrency());
        final ExpenseCategory category = ExpenseCategory.valueOf(entity.getCategory());

        return Expense.record(
                ExpenseId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                amount,
                category,
                entity.getDescription(),
                entity.getIncurredDate()
        );
    }
}
