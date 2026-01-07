package org.creatorledger.expense.application

import org.creatorledger.common.Money
import org.creatorledger.expense.api.ExpenseCategory
import org.creatorledger.expense.api.ExpenseId
import org.creatorledger.expense.domain.Expense
import org.creatorledger.expense.domain.ExpenseRecorded
import org.creatorledger.user.api.UserId
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

import java.time.LocalDate

class ExpenseApplicationServiceSpec extends Specification {

    ExpenseRepository expenseRepository
    ApplicationEventPublisher eventPublisher
    ExpenseApplicationService service

    def setup() {
        expenseRepository = Mock(ExpenseRepository)
        eventPublisher = Mock(ApplicationEventPublisher)
        service = new ExpenseApplicationService(expenseRepository, eventPublisher)
    }

    def "should record new expense"() {
        given: "a valid record command"
        def userId = UserId.generate()
        def command = new RecordExpenseCommand(
                userId,
                "150.00",
                "GBP",
                ExpenseCategory.EQUIPMENT,
                "MacBook Pro laptop",
                LocalDate.of(2026, 1, 15)
        )

        when: "recording the expense"
        def expenseId = service.record(command)

        then: "the expense should be saved to repository"
        1 * expenseRepository.save(_ as Expense) >> { Expense expense ->
            assert expense.userId() == userId
            assert expense.amount() == Money.gbp("150.00")
            assert expense.category() == ExpenseCategory.EQUIPMENT
            assert expense.description() == "MacBook Pro laptop"
            assert expense.incurredDate() == LocalDate.of(2026, 1, 15)
            return expense
        }
        expenseId != null
    }

    def "should publish ExpenseRecorded event when recording expense"() {
        given: "a valid record command"
        def userId = UserId.generate()
        def command = new RecordExpenseCommand(
                userId,
                "150.00",
                "GBP",
                ExpenseCategory.EQUIPMENT,
                "MacBook Pro laptop",
                LocalDate.of(2026, 1, 15)
        )

        when: "recording the expense"
        service.record(command)

        then: "the expense is saved"
        1 * expenseRepository.save(_ as Expense) >> { Expense expense -> expense }

        and: "ExpenseRecorded event is published"
        1 * eventPublisher.publishEvent(_) >> { arguments ->
            def event = arguments[0]
            assert event instanceof ExpenseRecorded
            assert event.userId() == userId
            assert event.amount() == Money.gbp("150.00")
            assert event.category() == ExpenseCategory.EQUIPMENT
            assert event.description() == "MacBook Pro laptop"
            assert event.incurredDate() == LocalDate.of(2026, 1, 15)
            assert event.occurredAt() != null
        }
    }

    def "should reject null command when recording"() {
        when: "recording with null command"
        service.record(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should update existing expense"() {
        given: "an existing expense and update command"
        def expenseId = ExpenseId.generate()
        def userId = UserId.generate()
        def existingExpense = Expense.record(
                expenseId,
                userId,
                Money.gbp("150.00"),
                ExpenseCategory.EQUIPMENT,
                "Original description",
                LocalDate.of(2026, 1, 15)
        )
        def command = new UpdateExpenseCommand(
                expenseId,
                "200.00",
                "GBP",
                ExpenseCategory.SOFTWARE,
                "Updated description",
                LocalDate.of(2026, 2, 20)
        )

        when: "updating the expense"
        service.update(command)

        then: "the existing expense should be retrieved"
        1 * expenseRepository.findById(expenseId) >> Optional.of(existingExpense)

        and: "the updated expense should be saved"
        1 * expenseRepository.save(_ as Expense) >> { Expense expense ->
            assert expense.id() == expenseId
            assert expense.userId() == userId
            assert expense.amount() == Money.gbp("200.00")
            assert expense.category() == ExpenseCategory.SOFTWARE
            assert expense.description() == "Updated description"
            assert expense.incurredDate() == LocalDate.of(2026, 2, 20)
            return expense
        }
    }

    def "should reject null command when updating"() {
        when: "updating with null command"
        service.update(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should throw when updating non-existent expense"() {
        given: "a command for non-existent expense"
        def expenseId = ExpenseId.generate()
        def command = new UpdateExpenseCommand(
                expenseId,
                "200.00",
                "GBP",
                ExpenseCategory.SOFTWARE,
                "Description",
                LocalDate.now()
        )

        when: "updating"
        service.update(command)

        then: "it should look for the expense"
        1 * expenseRepository.findById(expenseId) >> Optional.empty()

        and: "it should throw IllegalStateException"
        def exception = thrown(IllegalStateException)
        exception.message == "Expense not found: " + expenseId
    }

    def "should find expense by ID"() {
        given: "an existing expense"
        def expenseId = ExpenseId.generate()
        def expense = Expense.record(
                expenseId,
                UserId.generate(),
                Money.gbp("150.00"),
                ExpenseCategory.EQUIPMENT,
                "Description",
                LocalDate.now()
        )

        when: "finding by ID"
        def result = service.findById(expenseId)

        then: "it should query the repository"
        1 * expenseRepository.findById(expenseId) >> Optional.of(expense)
        result.isPresent()
        result.get().id() == expenseId
    }

    def "should reject null expenseId when finding"() {
        when: "finding with null ID"
        service.findById(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Expense ID cannot be null"
    }

    def "should check if expense exists by ID"() {
        given: "an expense ID"
        def expenseId = ExpenseId.generate()

        when: "checking existence"
        def result = service.existsById(expenseId)

        then: "it should query the repository"
        1 * expenseRepository.existsById(expenseId) >> true
        result == true
    }

    def "should reject null expenseId when checking existence"() {
        when: "checking existence with null ID"
        service.existsById(null)

        then: "it should throw IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Expense ID cannot be null"
    }
}
