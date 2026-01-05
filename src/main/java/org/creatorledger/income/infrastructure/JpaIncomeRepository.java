package org.creatorledger.income.infrastructure;

import org.creatorledger.income.application.IncomeRepository;
import org.creatorledger.income.domain.Income;
import org.creatorledger.income.api.IncomeId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA implementation of the IncomeRepository.
 * <p>
 * This is an adapter that bridges the application layer's repository port
 * with Spring Data JPA infrastructure. It handles conversion between domain
 * objects and JPA entities using the IncomeEntityMapper.
 * </p>
 */
@Repository
public class JpaIncomeRepository implements IncomeRepository {

    private final SpringDataIncomeRepository springDataRepository;

    public JpaIncomeRepository(SpringDataIncomeRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Income save(final Income income) {
        IncomeJpaEntity entity = IncomeEntityMapper.toEntity(income);
        IncomeJpaEntity saved = springDataRepository.save(entity);
        return IncomeEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Income> findById(final IncomeId id) {
        return springDataRepository.findById(id.value())
                .map(IncomeEntityMapper::toDomain);
    }

    @Override
    public boolean existsById(final IncomeId id) {
        return springDataRepository.existsById(id.value());
    }

    @Override
    public void delete(final Income income) {
        springDataRepository.deleteById(income.id().value());
    }
}
