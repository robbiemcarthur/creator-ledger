package org.creatorledger.expense.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataExpenseRepository extends JpaRepository<ExpenseJpaEntity, UUID> {
}
