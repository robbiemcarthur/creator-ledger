package org.creatorledger.income.infrastructure

import org.creatorledger.common.Money
import org.creatorledger.event.api.EventId
import org.creatorledger.income.api.IncomeId
import org.creatorledger.income.domain.Income
import org.creatorledger.user.api.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification
import java.time.LocalDate

@SpringBootTest
@Transactional
@Testcontainers
class JpaIncomeRepositoryIntegrationSpec extends Specification {

    @Shared
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

    @Autowired
    JpaIncomeRepository repository

    def "should save and retrieve an income"() {
        given: "a new income"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Website design project",
            LocalDate.of(2026, 1, 15)
        )

        when: "saving the income"
        def saved = repository.save(income)

        and: "retrieving the income by ID"
        def retrieved = repository.findById(income.id())

        then: "the income is persisted and retrieved correctly"
        saved != null
        saved.id() == income.id()
        saved.userId() == income.userId()
        saved.eventId() == income.eventId()
        saved.amount() == income.amount()
        saved.description() == income.description()
        saved.receivedDate() == income.receivedDate()
        saved.status() == income.status()

        and: "retrieved income matches saved income"
        retrieved.isPresent()
        retrieved.get().id() == income.id()
        retrieved.get().userId() == income.userId()
        retrieved.get().eventId() == income.eventId()
        retrieved.get().amount() == income.amount()
        retrieved.get().description() == income.description()
        retrieved.get().receivedDate() == income.receivedDate()
        retrieved.get().status() == income.status()
    }

    def "should return empty when income not found"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        when: "finding by ID"
        def result = repository.findById(incomeId)

        then: "empty is returned"
        result.isEmpty()
    }

    def "should check income existence"() {
        given: "a saved income"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("750.00"),
            "Existence test income",
            LocalDate.of(2026, 2, 20)
        )
        repository.save(income)

        when: "checking if income exists"
        def exists = repository.existsById(income.id())

        then: "true is returned"
        exists
    }

    def "should return false for non-existent income"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        when: "checking existence"
        def exists = repository.existsById(incomeId)

        then: "false is returned"
        !exists
    }

    def "should delete an income"() {
        given: "a saved income"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("300.00"),
            "Income to delete",
            LocalDate.now()
        )
        repository.save(income)

        when: "deleting the income"
        repository.delete(income)

        and: "checking if income still exists"
        def exists = repository.existsById(income.id())

        then: "income is deleted"
        !exists
    }

    def "should update an existing income"() {
        given: "a saved income"
        def incomeId = IncomeId.generate()
        def originalIncome = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Original description",
            LocalDate.of(2026, 1, 15)
        )
        repository.save(originalIncome)

        when: "updating the income with new details"
        def updatedIncome = originalIncome.update(
            Money.gbp("750.00"),
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )
        repository.save(updatedIncome)

        and: "retrieving the income"
        def retrieved = repository.findById(incomeId)

        then: "the income is updated"
        retrieved.isPresent()
        retrieved.get().amount() == Money.gbp("750.00")
        retrieved.get().description() == "Updated description"
        retrieved.get().receivedDate() == LocalDate.of(2026, 2, 20)
    }

    def "should persist and retrieve different payment statuses"() {
        given: "an income marked as paid"
        def income = Income.record(
            UserId.generate(),
            EventId.generate(),
            Money.gbp("600.00"),
            "Payment for status test",
            LocalDate.now()
        ).markAsPaid()

        when: "saving and retrieving"
        repository.save(income)
        def retrieved = repository.findById(income.id())

        then: "status is persisted correctly"
        retrieved.isPresent()
        retrieved.get().status() == org.creatorledger.income.domain.PaymentStatus.PAID
    }
}
