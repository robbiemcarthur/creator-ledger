package org.creatorledger.common

import spock.lang.Specification

class TaxYearSummaryIdSpec extends Specification {

    def "should create a valid tax year summary ID"() {
        given: "a valid UUID"
        def uuid = UUID.randomUUID()

        when: "a tax year summary ID is created"
        def id = TaxYearSummaryId.of(uuid)

        then: "it is created successfully"
        id != null
        id.value() == uuid
    }

    def "should generate a new random tax year summary ID"() {
        when: "a new ID is generated"
        def id = TaxYearSummaryId.generate()

        then: "it is created with a random UUID"
        id != null
        id.value() != null
    }

    def "should reject null UUID"() {
        when: "creating with null UUID"
        TaxYearSummaryId.of(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Tax year summary ID cannot be null"
    }

    def "should be equal when UUIDs are the same"() {
        given: "two IDs with the same UUID"
        def uuid = UUID.randomUUID()
        def id1 = TaxYearSummaryId.of(uuid)
        def id2 = TaxYearSummaryId.of(uuid)

        expect: "they are equal"
        id1 == id2
        id1.hashCode() == id2.hashCode()
    }

    def "should not be equal when UUIDs differ"() {
        given: "two IDs with different UUIDs"
        def id1 = TaxYearSummaryId.generate()
        def id2 = TaxYearSummaryId.generate()

        expect: "they are not equal"
        id1 != id2
    }

    def "should provide readable toString"() {
        given: "a tax year summary ID"
        def uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        def id = TaxYearSummaryId.of(uuid)

        when: "toString is called"
        def result = id.toString()

        then: "it contains the UUID"
        result == "TaxYearSummaryId[123e4567-e89b-12d3-a456-426614174000]"
    }
}
