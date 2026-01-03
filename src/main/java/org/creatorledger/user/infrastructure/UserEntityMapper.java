package org.creatorledger.user.infrastructure;

import org.creatorledger.user.domain.Email;
import org.creatorledger.user.domain.User;
import org.creatorledger.common.UserId;

/**
 * Mapper for converting between domain User and UserJpaEntity.
 * <p>
 * This keeps the domain model clean from persistence annotations and
 * allows the infrastructure to evolve independently from the domain.
 * </p>
 */
public class UserEntityMapper {

    /**
     * Converts a domain User to a JPA entity.
     *
     * @param user the domain user
     * @return the JPA entity
     */
    public static UserJpaEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserJpaEntity(
                user.id().value(),
                user.email().value()
        );
    }

    /**
     * Converts a JPA entity to a domain User.
     *
     * @param entity the JPA entity
     * @return the domain user
     */
    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.register(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail())
        );
    }
}
