package org.creatorledger.income.application;

import org.creatorledger.income.domain.Income;
import org.creatorledger.income.api.IncomeId;

import java.util.Optional;

public interface IncomeRepository {
    Income save(Income income);
    Optional<Income> findById(IncomeId id);
    boolean existsById(IncomeId id);
    void delete(Income income);
}
