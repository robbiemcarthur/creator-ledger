package org.creatorledger.common

import spock.lang.Specification

class EventIdSpec extends Specification {

    def "should create a valid EventId from UUID"() {
        given: "a valid UUID"
        def uuid = UUID.randomUUID()

        when: "creating an EventId"
        def eventId = EventId.of(uuid)

        then: "it should contain the UUID"
        eventId.value() == uuid
    }

    def "should generate a new EventId"() {
        when: "generating a new EventId"
        def eventId = EventId.generate()

        then: "it should have a non-null UUID"
        eventId.value() != null
    }

    def "should reject null UUID"() {
        when: "creating an EventId with null"
        EventId.of(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "EventId cannot be null"
    }

    def "should be equal when UUIDs are equal"() {
        given: "the same UUID"
        def uuid = UUID.randomUUID()

        when: "creating two EventIds with the same UUID"
        def eventId1 = EventId.of(uuid)
        def eventId2 = EventId.of(uuid)

        then: "they should be equal"
        eventId1 == eventId2
        eventId1.hashCode() == eventId2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "an EventId"
        def uuid = UUID.randomUUID()
        def eventId = EventId.of(uuid)

        when: "converting to string"
        def result = eventId.toString()

        then: "it should contain the UUID"
        result.contains(uuid.toString())
    }
}
