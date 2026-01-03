package org.creatorledger.user.domain;

import org.creatorledger.common.UserId;

import java.time.Instant;

/**
 * Domain event published when a user is registered.
 * This event can be consumed by other modules for cross-cutting concerns.
 */
public record UserRegistered(
    UserId userId,
    Email email,
    Instant occurredAt
) {

    /**
     * Creates a UserRegistered event with the current timestamp.
     *
     * @param userId the ID of the registered user
     * @param email the email of the registered user
     * @return a new UserRegistered event
     */
    public static UserRegistered of(UserId userId, Email email) {
        return new UserRegistered(userId, email, Instant.now());
    }

    @Override
    public String toString() {
        return "UserRegistered[userId=" + userId + ", email=" + email.value() +
               ", occurredAt=" + occurredAt + "]";
    }
}
