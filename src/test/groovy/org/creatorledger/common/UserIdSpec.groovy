package org.creatorledger.common

import spock.lang.Specification

class UserIdSpec extends Specification {

    def "should create a valid UserId from UUID"() {
        given: "a valid UUID"
        def uuid = UUID.randomUUID()

        when: "creating a UserId"
        def userId = UserId.of(uuid)

        then: "it should contain the UUID"
        userId.value() == uuid
    }

    def "should generate a new UserId"() {
        when: "generating a new UserId"
        def userId = UserId.generate()

        then: "it should have a non-null UUID"
        userId.value() != null
    }

    def "should reject null UUID"() {
        when: "creating a UserId with null"
        UserId.of(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "UserId cannot be null"
    }

    def "should be equal when UUIDs are equal"() {
        given: "the same UUID"
        def uuid = UUID.randomUUID()

        when: "creating two UserIds with the same UUID"
        def userId1 = UserId.of(uuid)
        def userId2 = UserId.of(uuid)

        then: "they should be equal"
        userId1 == userId2
        userId1.hashCode() == userId2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "a UserId"
        def uuid = UUID.randomUUID()
        def userId = UserId.of(uuid)

        when: "converting to string"
        def result = userId.toString()

        then: "it should contain the UUID"
        result.contains(uuid.toString())
    }
}
