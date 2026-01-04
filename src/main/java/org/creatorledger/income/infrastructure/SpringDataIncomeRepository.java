package org.creatorledger.income.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository interface for IncomeJpaEntity.
 * <p>
 * Spring Data automatically provides the implementation at runtime.
 * </p>
 */
interface SpringDataIncomeRepository extends JpaRepository<IncomeJpaEntity, UUID> {
}
