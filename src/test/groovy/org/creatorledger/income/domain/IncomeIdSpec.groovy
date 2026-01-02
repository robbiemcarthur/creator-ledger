package org.creatorledger.income.domain

import spock.lang.Specification

class IncomeIdSpec extends Specification {

    def "should create a valid IncomeId from UUID"() {
        given: "a valid UUID"
        def uuid = UUID.randomUUID()

        when: "creating an IncomeId"
        def incomeId = IncomeId.of(uuid)

        then: "it should contain the UUID"
        incomeId.value() == uuid
    }

    def "should generate a new IncomeId"() {
        when: "generating a new IncomeId"
        def incomeId = IncomeId.generate()

        then: "it should have a non-null UUID"
        incomeId.value() != null
    }

    def "should reject null UUID"() {
        when: "creating an IncomeId with null"
        IncomeId.of(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "IncomeId cannot be null"
    }

    def "should be equal when UUIDs are equal"() {
        given: "the same UUID"
        def uuid = UUID.randomUUID()

        when: "creating two IncomeIds with the same UUID"
        def incomeId1 = IncomeId.of(uuid)
        def incomeId2 = IncomeId.of(uuid)

        then: "they should be equal"
        incomeId1 == incomeId2
        incomeId1.hashCode() == incomeId2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "an IncomeId"
        def uuid = UUID.randomUUID()
        def incomeId = IncomeId.of(uuid)

        when: "converting to string"
        def result = incomeId.toString()

        then: "it should contain the UUID"
        result.contains(uuid.toString())
    }
}
