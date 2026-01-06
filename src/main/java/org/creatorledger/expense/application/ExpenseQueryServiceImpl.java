package org.creatorledger.expense.application;

import org.creatorledger.expense.api.ExpenseData;
import org.creatorledger.expense.api.ExpenseQueryService;
import org.creatorledger.user.api.UserId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseQueryServiceImpl implements ExpenseQueryService {

    private final ExpenseRepository expenseRepository;

    public ExpenseQueryServiceImpl(final ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public List<ExpenseData> findByUserIdAndDateRange(final UserId userId, final LocalDate startDate, final LocalDate endDate) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return expenseRepository.findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(ExpenseData::from)
                .toList();
    }
}
