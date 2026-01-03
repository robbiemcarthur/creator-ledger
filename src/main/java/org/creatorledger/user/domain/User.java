package org.creatorledger.user.domain;

import org.creatorledger.common.UserId;

/**
 * Aggregate root representing a user in the system.
 * Users are identified by their UserId and have an email address.
 */
public record User(UserId id, Email email) {

    /**
     * Registers a new user with a generated ID.
     *
     * @param email the user's email address
     * @return a new User
     * @throws IllegalArgumentException if email is null
     */
    public static User register(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return new User(UserId.generate(), email);
    }

    /**
     * Registers a new user with a specific ID.
     *
     * @param id the user ID
     * @param email the user's email address
     * @return a new User
     * @throws IllegalArgumentException if id or email is null
     */
    public static User register(UserId id, Email email) {
        if (id == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return new User(id, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User other)) return false;
        // Entity equality is based on ID only
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        // Entity hashCode is based on ID only
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User[id=" + id + ", email=" + email.value() + "]";
    }
}
