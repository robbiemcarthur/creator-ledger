package org.creatorledger.user.domain

import org.creatorledger.common.UserId
import spock.lang.Specification
import java.time.Instant

class UserRegisteredSpec extends Specification {

    def "should create UserRegistered event"() {
        given: "a user ID and email"
        def userId = UserId.generate()
        def email = Email.of("user@example.com")
        def occurredAt = Instant.now()

        when: "creating a UserRegistered event"
        def event = new UserRegistered(userId, email, occurredAt)

        then: "it should contain all the data"
        event.userId() == userId
        event.email() == email
        event.occurredAt() == occurredAt
    }

    def "should create UserRegistered event with current timestamp"() {
        given: "a user ID and email"
        def userId = UserId.generate()
        def email = Email.of("user@example.com")
        def before = Instant.now()

        when: "creating a UserRegistered event without timestamp"
        def event = UserRegistered.of(userId, email)
        def after = Instant.now()

        then: "it should have a timestamp between before and after"
        event.userId() == userId
        event.email() == email
        !event.occurredAt().isBefore(before)
        !event.occurredAt().isAfter(after)
    }

    def "should be equal when all fields are equal"() {
        given: "the same event data"
        def userId = UserId.generate()
        def email = Email.of("user@example.com")
        def occurredAt = Instant.now()

        when: "creating two events with same data"
        def event1 = new UserRegistered(userId, email, occurredAt)
        def event2 = new UserRegistered(userId, email, occurredAt)

        then: "they should be equal"
        event1 == event2
        event1.hashCode() == event2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "a UserRegistered event"
        def userId = UserId.generate()
        def email = Email.of("user@example.com")
        def event = UserRegistered.of(userId, email)

        when: "converting to string"
        def result = event.toString()

        then: "it should contain the userId and email"
        result.contains(userId.toString())
        result.contains(email.value())
    }
}
