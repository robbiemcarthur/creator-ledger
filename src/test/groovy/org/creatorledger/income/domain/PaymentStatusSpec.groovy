package org.creatorledger.income.domain

import spock.lang.Specification

class PaymentStatusSpec extends Specification {

    def "should create PENDING payment status"() {
        when: "creating PENDING status"
        def status = PaymentStatus.PENDING

        then: "it should be PENDING"
        status.name() == "PENDING"
        !status.isPaid()
    }

    def "should create PAID payment status"() {
        when: "creating PAID status"
        def status = PaymentStatus.PAID

        then: "it should be PAID"
        status.name() == "PAID"
        status.isPaid()
    }

    def "should create OVERDUE payment status"() {
        when: "creating OVERDUE status"
        def status = PaymentStatus.OVERDUE

        then: "it should be OVERDUE"
        status.name() == "OVERDUE"
        !status.isPaid()
    }

    def "should create CANCELLED payment status"() {
        when: "creating CANCELLED status"
        def status = PaymentStatus.CANCELLED

        then: "it should be CANCELLED"
        status.name() == "CANCELLED"
        !status.isPaid()
    }

    def "should correctly identify paid status"() {
        expect: "only PAID status is paid"
        PaymentStatus.PAID.isPaid()
        !PaymentStatus.PENDING.isPaid()
        !PaymentStatus.OVERDUE.isPaid()
        !PaymentStatus.CANCELLED.isPaid()
    }

    def "should have meaningful toString"() {
        expect: "toString returns status name"
        PaymentStatus.PENDING.toString() == "PENDING"
        PaymentStatus.PAID.toString() == "PAID"
        PaymentStatus.OVERDUE.toString() == "OVERDUE"
        PaymentStatus.CANCELLED.toString() == "CANCELLED"
    }
}
