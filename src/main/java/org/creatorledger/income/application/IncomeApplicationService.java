package org.creatorledger.income.application;

import org.creatorledger.income.domain.Income;
import org.creatorledger.income.api.IncomeId;
import org.creatorledger.common.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class IncomeApplicationService {

    private final IncomeRepository incomeRepository;

    public IncomeApplicationService(final IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public IncomeId record(final RecordIncomeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        final Money amount = Money.of(new BigDecimal(command.amount()), command.currency());
        final Income income = Income.record(
                IncomeId.generate(),
                command.userId(),
                command.eventId(),
                amount,
                command.description(),
                command.receivedDate()
        );

        incomeRepository.save(income);
        return income.id();
    }

    public void update(final UpdateIncomeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        final Income existingIncome = incomeRepository.findById(command.incomeId())
                .orElseThrow(() -> new IllegalStateException("Income not found: " + command.incomeId()));

        final Money amount = Money.of(new BigDecimal(command.amount()), command.currency());
        final Income updatedIncome = existingIncome.update(
                amount,
                command.description(),
                command.receivedDate()
        );

        incomeRepository.save(updatedIncome);
    }

    public void markAsPaid(final IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalStateException("Income not found: " + incomeId));

        Income paidIncome = income.markAsPaid();
        incomeRepository.save(paidIncome);
    }

    public void markAsOverdue(final IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalStateException("Income not found: " + incomeId));

        Income overdueIncome = income.markAsOverdue();
        incomeRepository.save(overdueIncome);
    }

    public void cancel(final IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalStateException("Income not found: " + incomeId));

        Income cancelledIncome = income.cancel();
        incomeRepository.save(cancelledIncome);
    }

    public Optional<Income> findById(final IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        return incomeRepository.findById(incomeId);
    }
    
    public boolean existsById(final IncomeId incomeId) {
        if (incomeId == null) {
            throw new IllegalArgumentException("Income ID cannot be null");
        }

        return incomeRepository.existsById(incomeId);
    }
}
