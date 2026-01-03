package org.creatorledger.user.application;

import org.creatorledger.user.domain.Email;
import org.creatorledger.user.domain.User;
import org.creatorledger.user.domain.UserId;

import java.util.Optional;

/**
 * Application service for user-related use cases.
 * <p>
 * This service orchestrates domain operations and coordinates with the repository
 * to persist changes. It serves as the entry point for user-related commands
 * and queries from the presentation layer.
 * </p>
 */
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserApplicationService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("User repository cannot be null");
        }
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user in the system.
     *
     * @param command the registration command containing user details
     * @return the ID of the newly registered user
     * @throws IllegalArgumentException if command is null
     */
    public UserId register(RegisterUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        Email email = Email.of(command.email());
        User user = User.register(email);

        userRepository.save(user);

        return user.id();
    }

    /**
     * Finds a user by their unique identifier.
     *
     * @param userId the user ID
     * @return an Optional containing the user if found, empty otherwise
     * @throws IllegalArgumentException if userId is null
     */
    public Optional<User> findById(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(userId);
    }

    /**
     * Checks if a user exists with the given ID.
     *
     * @param userId the user ID
     * @return true if a user exists, false otherwise
     * @throws IllegalArgumentException if userId is null
     */
    public boolean existsById(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.existsById(userId);
    }
}
