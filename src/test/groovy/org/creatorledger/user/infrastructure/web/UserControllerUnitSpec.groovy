package org.creatorledger.user.infrastructure.web

import org.creatorledger.user.application.UserApplicationService
import org.creatorledger.user.domain.Email
import org.creatorledger.user.domain.User
import org.creatorledger.common.UserId
import org.springframework.http.HttpStatus
import spock.lang.Specification

class UserControllerUnitSpec extends Specification {

    UserApplicationService userApplicationService
    UserController controller

    def setup() {
        userApplicationService = Mock(UserApplicationService)
        controller = new UserController(userApplicationService)
    }

    def "should register a new user"() {
        given: "a register user request"
        def request = new RegisterUserRequest("test@example.com")

        and: "the application service will return a user ID"
        def userId = UserId.generate()
        userApplicationService.register(_) >> userId

        when: "registering the user"
        def response = controller.register(request)

        then: "the response is 201 Created with location header"
        response.statusCode == HttpStatus.CREATED
        response.headers.getLocation().toString() == "/api/users/${userId.value()}"
    }

    def "should return 400 when registration fails with invalid data"() {
        given: "an invalid request"
        def request = new RegisterUserRequest("test@example.com")

        and: "the application service throws exception"
        userApplicationService.register(_) >> { throw new IllegalArgumentException("Invalid email") }

        when: "registering the user"
        def response = controller.register(request)

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should get user by ID"() {
        given: "an existing user"
        def userId = UserId.generate()
        def user = User.register(userId, Email.of("test@example.com"))

        and: "the application service returns the user"
        userApplicationService.findById(userId) >> Optional.of(user)

        when: "getting the user"
        def response = controller.getUser(userId.value().toString())

        then: "the response is 200 OK with user data"
        response.statusCode == HttpStatus.OK
        response.body.id() == userId.value().toString()
        response.body.email() == "test@example.com"
    }

    def "should return 404 when user not found"() {
        given: "a non-existent user ID"
        def userId = UserId.generate()

        and: "the application service returns empty"
        userApplicationService.findById(userId) >> Optional.empty()

        when: "getting the user"
        def response = controller.getUser(userId.value().toString())

        then: "the response is 404 Not Found"
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return 400 for invalid UUID format"() {
        when: "getting user with invalid UUID"
        def response = controller.getUser("invalid-uuid")

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should check if user exists"() {
        given: "an existing user ID"
        def userId = UserId.generate()

        and: "the application service confirms existence"
        userApplicationService.existsById(userId) >> true

        when: "checking existence"
        def response = controller.existsById(userId.value().toString())

        then: "the response is 200 OK with true"
        response.statusCode == HttpStatus.OK
        response.body == true
    }

    def "should return false when user does not exist"() {
        given: "a non-existent user ID"
        def userId = UserId.generate()

        and: "the application service returns false"
        userApplicationService.existsById(userId) >> false

        when: "checking existence"
        def response = controller.existsById(userId.value().toString())

        then: "the response is 200 OK with false"
        response.statusCode == HttpStatus.OK
        response.body == false
    }
}
