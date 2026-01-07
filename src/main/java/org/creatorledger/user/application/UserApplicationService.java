package org.creatorledger.user.application;

import org.creatorledger.user.api.UserId;
import org.creatorledger.user.domain.Email;
import org.creatorledger.user.domain.User;
import org.creatorledger.user.domain.UserRegistered;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service for user-related use cases.
 * <p>
 * This service orchestrates domain operations and coordinates with the repository
 * to persist changes. It serves as the entry point for user-related commands
 * and queries from the presentation layer.
 * </p>
 */
@Service
public class UserApplicationService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserApplicationService(UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        if (userRepository == null) {
            throw new IllegalArgumentException("User repository cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("Event publisher cannot be null");
        }
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public UserId register(final RegisterUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        Email email = Email.of(command.email());
        User user = User.register(email);

        userRepository.save(user);

        // Publish domain event
        final UserRegistered event = UserRegistered.of(user.id(), user.email());
        eventPublisher.publishEvent(event);

        return user.id();
    }

    public Optional<User> findById(final UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(userId);
    }

    public boolean existsById(final UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.existsById(userId);
    }
}
