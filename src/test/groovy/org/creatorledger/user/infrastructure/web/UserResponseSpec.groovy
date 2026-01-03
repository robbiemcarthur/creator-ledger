package org.creatorledger.user.infrastructure.web

import org.creatorledger.user.domain.Email
import org.creatorledger.user.domain.User
import org.creatorledger.common.UserId
import spock.lang.Specification

class UserResponseSpec extends Specification {

    def "should create a valid user response"() {
        given: "a user ID and email"
        def userId = UUID.randomUUID().toString()
        def email = "user@example.com"

        when: "a response is created"
        def response = new UserResponse(userId, email)

        then: "it is created successfully"
        response != null
        response.id() == userId
        response.email() == email
    }

    def "should create response from domain user"() {
        given: "a domain user"
        def user = User.register(Email.of("user@example.com"))

        when: "creating response from user"
        def response = UserResponse.from(user)

        then: "response contains user data"
        response.id() == user.id().value().toString()
        response.email() == user.email().value()
    }

    def "should reject null user when creating from domain"() {
        when: "creating response from null user"
        UserResponse.from(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "User cannot be null"
    }

    def "should be equal when IDs and emails are the same"() {
        given: "two responses with same data"
        def id = UUID.randomUUID().toString()
        def response1 = new UserResponse(id, "user@example.com")
        def response2 = new UserResponse(id, "user@example.com")

        expect: "they are equal"
        response1 == response2
        response1.hashCode() == response2.hashCode()
    }
}
