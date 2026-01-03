package org.creatorledger.user.infrastructure.web;

import org.creatorledger.user.application.RegisterUserCommand;
import org.creatorledger.user.application.UserApplicationService;
import org.creatorledger.user.domain.User;
import org.creatorledger.common.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for user-related endpoints.
 * <p>
 * This controller provides HTTP API access to user functionality,
 * translating HTTP requests/responses to application commands/queries.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user details
     * @return 201 Created with Location header pointing to the new user
     */
    @PostMapping
    public ResponseEntity<Void> register(@RequestBody RegisterUserRequest request) {
        try {
            RegisterUserCommand command = new RegisterUserCommand(request.email());
            UserId userId = userApplicationService.register(command);

            URI location = URI.create("/api/users/" + userId.value());
            return ResponseEntity.created(location).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets a user by their ID.
     *
     * @param id the user ID
     * @return 200 OK with user data if found, 404 Not Found otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            UserId userId = UserId.of(uuid);

            return userApplicationService.findById(userId)
                    .map(UserResponse::from)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Checks if a user exists with the given ID.
     *
     * @param id the user ID
     * @return 200 OK with boolean indicating existence
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            UserId userId = UserId.of(uuid);

            boolean exists = userApplicationService.existsById(userId);
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
