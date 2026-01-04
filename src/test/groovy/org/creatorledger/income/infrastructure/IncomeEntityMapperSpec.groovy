package org.creatorledger.income.infrastructure

import org.creatorledger.common.Money
import org.creatorledger.event.api.EventId
import org.creatorledger.income.api.IncomeId
import org.creatorledger.income.domain.Income
import org.creatorledger.income.domain.PaymentStatus
import org.creatorledger.user.api.UserId
import spock.lang.Specification
import java.time.LocalDate

class IncomeEntityMapperSpec extends Specification {

    def "should map domain income to JPA entity"() {
        given: "a domain income"
        def incomeId = IncomeId.generate()
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def amount = Money.gbp("500.00")
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)
        def income = Income.record(incomeId, userId, eventId, amount, description, receivedDate)

        when: "mapping to entity"
        def entity = IncomeEntityMapper.toEntity(income)

        then: "entity is mapped correctly"
        entity != null
        entity.id == incomeId.value()
        entity.userId == userId.value()
        entity.eventId == eventId.value()
        entity.amount == amount.amount()
        entity.currency == amount.currency()
        entity.description == description
        entity.receivedDate == receivedDate
        entity.status == PaymentStatus.PENDING.name()
    }

    def "should map JPA entity to domain income"() {
        given: "a JPA entity"
        def id = UUID.randomUUID()
        def userId = UUID.randomUUID()
        def eventId = UUID.randomUUID()
        def amount = new BigDecimal("500.00")
        def currency = "GBP"
        def description = "Website design project"
        def receivedDate = LocalDate.of(2026, 1, 15)
        def status = "PENDING"
        def entity = new IncomeJpaEntity(id, userId, eventId, amount, currency, description, receivedDate, status)

        when: "mapping to domain"
        def income = IncomeEntityMapper.toDomain(entity)

        then: "income is mapped correctly"
        income != null
        income.id().value() == id
        income.userId().value() == userId
        income.eventId().value() == eventId
        income.amount() == Money.of(amount, currency)
        income.description() == description
        income.receivedDate() == receivedDate
        income.status() == PaymentStatus.PENDING
    }

    def "should handle null when mapping to entity"() {
        when: "mapping null to entity"
        def result = IncomeEntityMapper.toEntity(null)

        then: "null is returned"
        result == null
    }

    def "should handle null when mapping to domain"() {
        when: "mapping null to domain"
        def result = IncomeEntityMapper.toDomain(null)

        then: "null is returned"
        result == null
    }

    def "should round-trip correctly"() {
        given: "a domain income"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("750.00"),
            "Roundtrip test income",
            LocalDate.of(2026, 2, 20)
        )

        when: "converting to entity and back"
        def entity = IncomeEntityMapper.toEntity(income)
        def reconstituted = IncomeEntityMapper.toDomain(entity)

        then: "income is preserved"
        reconstituted.id() == income.id()
        reconstituted.userId() == income.userId()
        reconstituted.eventId() == income.eventId()
        reconstituted.amount() == income.amount()
        reconstituted.description() == income.description()
        reconstituted.receivedDate() == income.receivedDate()
        reconstituted.status() == income.status()
    }

    def "should map different payment statuses correctly"() {
        given: "an income with PAID status"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("300.00"),
            "Paid income",
            LocalDate.now()
        ).markAsPaid()

        when: "converting to entity and back"
        def entity = IncomeEntityMapper.toEntity(income)
        def reconstituted = IncomeEntityMapper.toDomain(entity)

        then: "status is preserved"
        entity.status == "PAID"
        reconstituted.status() == PaymentStatus.PAID
    }
}
