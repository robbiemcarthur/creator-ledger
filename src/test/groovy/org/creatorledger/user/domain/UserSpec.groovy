package org.creatorledger.user.domain

import org.creatorledger.common.UserId
import spock.lang.Specification

class UserSpec extends Specification {

    def "should register a new user with generated ID"() {
        given: "a valid email"
        def email = Email.of("user@example.com")

        when: "registering a new user"
        def user = User.register(email)

        then: "the user should have an ID and email"
        user.id() != null
        user.email() == email
    }

    def "should register a new user with specific ID"() {
        given: "a user ID and email"
        def userId = UserId.generate()
        def email = Email.of("user@example.com")

        when: "registering a user with specific ID"
        def user = User.register(userId, email)

        then: "the user should have the specified ID and email"
        user.id() == userId
        user.email() == email
    }

    def "should reject null email when registering"() {
        when: "registering a user with null email"
        User.register(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Email cannot be null"
    }

    def "should reject null ID when registering with ID"() {
        given: "a valid email"
        def email = Email.of("user@example.com")

        when: "registering a user with null ID"
        User.register(null, email)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "UserId cannot be null"
    }

    def "should be equal when IDs are equal"() {
        given: "the same user ID and different emails"
        def userId = UserId.generate()
        def email1 = Email.of("user1@example.com")
        def email2 = Email.of("user2@example.com")

        when: "creating two users with the same ID"
        def user1 = User.register(userId, email1)
        def user2 = User.register(userId, email2)

        then: "they should be equal (entity equality is based on ID)"
        user1 == user2
        user1.hashCode() == user2.hashCode()
    }

    def "should not be equal when IDs are different"() {
        given: "different user IDs but same email"
        def email = Email.of("user@example.com")

        when: "creating two users with different IDs"
        def user1 = User.register(email)
        def user2 = User.register(email)

        then: "they should not be equal"
        user1 != user2
    }

    def "should have meaningful toString"() {
        given: "a user"
        def email = Email.of("user@example.com")
        def user = User.register(email)

        when: "converting to string"
        def result = user.toString()

        then: "it should contain ID and email"
        result.contains(user.id().toString())
        result.contains(email.value())
    }
}
