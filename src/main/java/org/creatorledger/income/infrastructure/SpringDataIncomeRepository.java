package org.creatorledger.income.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataIncomeRepository extends JpaRepository<IncomeJpaEntity, UUID> {
}
