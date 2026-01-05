package org.creatorledger.expense.infrastructure

import org.creatorledger.common.Money
import org.creatorledger.expense.api.ExpenseCategory
import org.creatorledger.expense.api.ExpenseId
import org.creatorledger.expense.domain.Expense
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
class JpaExpenseRepositoryIntegrationSpec extends Specification {

    @Shared
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

    @Autowired
    JpaExpenseRepository repository

    def "should save and retrieve an expense"() {
        given: "a new expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "MacBook Pro laptop",
            LocalDate.of(2026, 1, 15)
        )

        when: "saving the expense"
        def saved = repository.save(expense)

        and: "retrieving the expense by ID"
        def retrieved = repository.findById(expense.id())

        then: "the expense is persisted and retrieved correctly"
        saved != null
        saved.id() == expense.id()
        saved.userId() == expense.userId()
        saved.amount() == expense.amount()
        saved.category() == expense.category()
        saved.description() == expense.description()
        saved.incurredDate() == expense.incurredDate()

        and: "retrieved expense matches saved expense"
        retrieved.isPresent()
        retrieved.get().id() == expense.id()
        retrieved.get().userId() == expense.userId()
        retrieved.get().amount() == expense.amount()
        retrieved.get().category() == expense.category()
        retrieved.get().description() == expense.description()
        retrieved.get().incurredDate() == expense.incurredDate()
    }

    def "should return empty when expense not found"() {
        given: "a non-existent expense ID"
        def expenseId = ExpenseId.generate()

        when: "finding by ID"
        def result = repository.findById(expenseId)

        then: "empty is returned"
        result.isEmpty()
    }

    def "should check if expense exists"() {
        given: "a saved expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("200.00"),
            ExpenseCategory.SOFTWARE,
            "Adobe Creative Cloud",
            LocalDate.now()
        )
        repository.save(expense)

        when: "checking if expense exists"
        def exists = repository.existsById(expense.id())
        def notExists = repository.existsById(ExpenseId.generate())

        then: "existence is correctly reported"
        exists == true
        notExists == false
    }

    def "should update an existing expense"() {
        given: "a saved expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "Original description",
            LocalDate.of(2026, 1, 15)
        )
        repository.save(expense)

        when: "updating the expense"
        def updated = expense.update(
            Money.gbp("200.00"),
            ExpenseCategory.SOFTWARE,
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )
        repository.save(updated)

        and: "retrieving the updated expense"
        def retrieved = repository.findById(expense.id())

        then: "the updated values are persisted"
        retrieved.isPresent()
        retrieved.get().id() == expense.id()
        retrieved.get().amount() == Money.gbp("200.00")
        retrieved.get().category() == ExpenseCategory.SOFTWARE
        retrieved.get().description() == "Updated description"
        retrieved.get().incurredDate() == LocalDate.of(2026, 2, 20)
    }

    def "should delete an expense"() {
        given: "a saved expense"
        def expense = Expense.record(
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "To be deleted",
            LocalDate.now()
        )
        repository.save(expense)

        when: "deleting the expense"
        repository.delete(expense)

        and: "attempting to retrieve the deleted expense"
        def result = repository.findById(expense.id())

        then: "the expense is no longer found"
        result.isEmpty()
    }
}
