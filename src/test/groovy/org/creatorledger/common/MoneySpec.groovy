package org.creatorledger.common

import spock.lang.Specification
import java.math.BigDecimal

class MoneySpec extends Specification {

    def "should create Money in GBP"() {
        given: "a valid amount"
        def amount = new BigDecimal("100.50")

        when: "creating Money in GBP"
        def money = Money.gbp(amount)

        then: "it should contain the amount and currency"
        money.amount() == amount
        money.currency() == "GBP"
    }

    def "should create Money from string amount"() {
        when: "creating Money from string"
        def money = Money.gbp("250.75")

        then: "it should parse correctly"
        money.amount() == new BigDecimal("250.75")
        money.currency() == "GBP"
    }

    def "should reject null amount"() {
        when: "creating Money with null amount"
        Money.gbp(null as BigDecimal)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Money amount cannot be null"
    }

    def "should reject negative amount"() {
        when: "creating Money with negative amount"
        Money.gbp(new BigDecimal("-10.00"))

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Money amount cannot be negative"
    }

    def "should accept zero amount"() {
        when: "creating Money with zero amount"
        def money = Money.gbp(BigDecimal.ZERO)

        then: "it should be created successfully"
        money.amount() == BigDecimal.ZERO
    }

    def "should scale to 2 decimal places"() {
        when: "creating Money with more than 2 decimal places"
        def money = Money.gbp("100.123")

        then: "it should round to 2 decimal places"
        money.amount() == new BigDecimal("100.12")
    }

    def "should add Money amounts"() {
        given: "two Money amounts"
        def money1 = Money.gbp("100.50")
        def money2 = Money.gbp("50.25")

        when: "adding them together"
        def result = money1.add(money2)

        then: "it should sum the amounts"
        result.amount() == new BigDecimal("150.75")
        result.currency() == "GBP"
    }

    def "should subtract Money amounts"() {
        given: "two Money amounts"
        def money1 = Money.gbp("100.50")
        def money2 = Money.gbp("50.25")

        when: "subtracting one from the other"
        def result = money1.subtract(money2)

        then: "it should calculate the difference"
        result.amount() == new BigDecimal("50.25")
        result.currency() == "GBP"
    }

    def "should multiply Money by factor"() {
        given: "a Money amount"
        def money = Money.gbp("100.00")

        when: "multiplying by 1.5"
        def result = money.multiply(new BigDecimal("1.5"))

        then: "it should calculate correctly"
        result.amount() == new BigDecimal("150.00")
        result.currency() == "GBP"
    }

    def "should reject currency mismatch in operations"() {
        given: "Money in different currencies"
        def gbp = Money.gbp("100.00")
        def usd = Money.of(new BigDecimal("100.00"), "USD")

        when: "trying to add different currencies"
        gbp.add(usd)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Cannot perform operation on different currencies: GBP and USD"
    }

    def "should compare Money amounts"() {
        given: "different Money amounts"
        def smaller = Money.gbp("50.00")
        def larger = Money.gbp("100.00")

        expect: "correct comparisons"
        smaller.isLessThan(larger)
        larger.isGreaterThan(smaller)
        !smaller.isGreaterThan(larger)
        !larger.isLessThan(smaller)
    }

    def "should check if Money is zero"() {
        expect: "correct zero checks"
        Money.gbp("0.00").isZero()
        !Money.gbp("0.01").isZero()
    }

    def "should check if Money is positive"() {
        expect: "correct positive checks"
        Money.gbp("100.00").isPositive()
        !Money.gbp("0.00").isPositive()
    }

    def "should be equal when amount and currency are equal"() {
        given: "the same amount and currency"
        def money1 = Money.gbp("100.50")
        def money2 = Money.gbp("100.50")

        expect: "they should be equal"
        money1 == money2
        money1.hashCode() == money2.hashCode()
    }

    def "should have meaningful toString"() {
        given: "Money"
        def money = Money.gbp("100.50")

        when: "converting to string"
        def result = money.toString()

        then: "it should contain amount and currency"
        result.contains("100.50")
        result.contains("GBP")
    }
}
