package org.creatorledger.user.application

import org.creatorledger.user.domain.Email
import org.creatorledger.user.domain.User
import org.creatorledger.user.domain.UserRegistered
import org.creatorledger.user.api.UserId
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

class UserApplicationServiceSpec extends Specification {

    UserRepository userRepository
    ApplicationEventPublisher eventPublisher
    UserApplicationService service

    def setup() {
        userRepository = Mock(UserRepository)
        eventPublisher = Mock(ApplicationEventPublisher)
        service = new UserApplicationService(userRepository, eventPublisher)
    }

    def "should register a new user"() {
        given: "a register user command"
        def command = new RegisterUserCommand("user@example.com")

        when: "registering the user"
        def userId = service.register(command)

        then: "the user is saved via repository"
        1 * userRepository.save(_) >> { User user ->
            assert user != null
            assert user.email() == Email.of("user@example.com")
            return user
        }

        and: "the user ID is returned"
        userId != null
    }

    def "should publish UserRegistered event when registering user"() {
        given: "a register user command"
        def command = new RegisterUserCommand("user@example.com")

        when: "registering the user"
        service.register(command)

        then: "the user is saved"
        1 * userRepository.save(_) >> { User user -> user }

        and: "UserRegistered event is published"
        1 * eventPublisher.publishEvent(_) >> { arguments ->
            def event = arguments[0]
            assert event instanceof UserRegistered
            assert event.email().value() == "user@example.com"
            assert event.occurredAt() != null
        }
    }

    def "should reject null command"() {
        when: "registering with null command"
        service.register(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should find user by ID when exists"() {
        given: "an existing user ID"
        def userId = UserId.generate()
        def existingUser = User.register(userId, Email.of("user@example.com"))

        and: "repository returns the user"
        userRepository.findById(userId) >> Optional.of(existingUser)

        when: "finding the user by ID"
        def result = service.findById(userId)

        then: "the user is returned"
        result.isPresent()
        result.get() == existingUser
    }

    def "should return empty when user not found"() {
        given: "a non-existent user ID"
        def userId = UserId.generate()

        and: "repository returns empty"
        userRepository.findById(userId) >> Optional.empty()

        when: "finding the user by ID"
        def result = service.findById(userId)

        then: "empty is returned"
        result.isEmpty()
    }

    def "should reject null user ID when finding"() {
        when: "finding with null ID"
        service.findById(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "User ID cannot be null"
    }

    def "should check if user exists"() {
        given: "an existing user ID"
        def userId = UserId.generate()

        and: "repository confirms existence"
        userRepository.existsById(userId) >> true

        when: "checking if user exists"
        def exists = service.existsById(userId)

        then: "true is returned"
        exists
    }

    def "should return false when user does not exist"() {
        given: "a non-existent user ID"
        def userId = UserId.generate()

        and: "repository returns false"
        userRepository.existsById(userId) >> false

        when: "checking if user exists"
        def exists = service.existsById(userId)

        then: "false is returned"
        !exists
    }

    def "should reject null user ID when checking existence"() {
        when: "checking existence with null ID"
        service.existsById(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "User ID cannot be null"
    }
}
