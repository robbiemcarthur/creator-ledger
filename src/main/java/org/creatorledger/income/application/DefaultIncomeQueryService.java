package org.creatorledger.income.application;

import org.creatorledger.income.api.IncomeData;
import org.creatorledger.income.api.IncomeQueryService;
import org.creatorledger.user.api.UserId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DefaultIncomeQueryService implements IncomeQueryService {

    private final IncomeRepository incomeRepository;

    public DefaultIncomeQueryService(final IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public List<IncomeData> findByUserIdAndDateRange(final UserId userId, final LocalDate startDate, final LocalDate endDate) {
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

        return incomeRepository.findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(IncomeData::from)
                .toList();
    }
}
