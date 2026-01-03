package org.creatorledger.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository interface for UserJpaEntity.
 * <p>
 * Spring Data automatically provides the implementation at runtime.
 * </p>
 */
interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {
}
