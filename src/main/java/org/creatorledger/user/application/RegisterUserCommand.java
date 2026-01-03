package org.creatorledger.user.application;

/**
 * Command to register a new user.
 * <p>
 * This command is used by the application layer to encapsulate the intent
 * to register a new user with the system.
 * </p>
 *
 * @param email the email address for the new user
 */
public record RegisterUserCommand(String email) {

    /**
     * Creates a new RegisterUserCommand.
     *
     * @throws IllegalArgumentException if email is null or blank
     */
    public RegisterUserCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }

    @Override
    public String toString() {
        return "RegisterUserCommand[email=%s]".formatted(email);
    }
}
