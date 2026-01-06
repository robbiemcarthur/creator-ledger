package org.creatorledger.income.application

import org.creatorledger.event.api.EventId
import org.creatorledger.income.api.IncomeId
import org.creatorledger.income.domain.Income
import org.creatorledger.income.api.PaymentStatus
import org.creatorledger.common.Money
import org.creatorledger.user.api.UserId
import spock.lang.Specification
import java.time.LocalDate

class IncomeApplicationServiceSpec extends Specification {

    IncomeRepository incomeRepository
    IncomeApplicationService service

    def setup() {
        incomeRepository = Mock(IncomeRepository)
        service = new IncomeApplicationService(incomeRepository)
    }

    def "should record new income"() {
        given: "a record income command"
        def command = new RecordIncomeCommand(
            UserId.generate(),
            EventId.generate(),
            "500.00",
            "GBP",
            "Website design project",
            LocalDate.of(2026, 1, 15)
        )

        when: "recording the income"
        def incomeId = service.record(command)

        then: "the income is saved via repository"
        1 * incomeRepository.save(_) >> { Income income ->
            assert income != null
            assert income.amount() == Money.gbp("500.00")
            assert income.description() == "Website design project"
            assert income.status() == PaymentStatus.PENDING
            return income
        }

        and: "the income ID is returned"
        incomeId != null
    }

    def "should reject null command when recording"() {
        when: "recording with null command"
        service.record(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should update existing income"() {
        given: "an existing income"
        def incomeId = IncomeId.generate()
        def existingIncome = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Original description",
            LocalDate.of(2026, 1, 15)
        )

        and: "repository returns the existing income"
        incomeRepository.findById(incomeId) >> Optional.of(existingIncome)

        and: "an update command"
        def command = new UpdateIncomeCommand(
            incomeId,
            "750.00",
            "GBP",
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )

        when: "updating the income"
        service.update(command)

        then: "the income is saved with updated details"
        1 * incomeRepository.save(_) >> { Income income ->
            assert income.id() == incomeId
            assert income.amount() == Money.gbp("750.00")
            assert income.description() == "Updated description"
            assert income.receivedDate() == LocalDate.of(2026, 2, 20)
            return income
        }
    }

    def "should reject null command when updating"() {
        when: "updating with null command"
        service.update(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Command cannot be null"
    }

    def "should throw exception when updating non-existent income"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        and: "repository returns empty"
        incomeRepository.findById(incomeId) >> Optional.empty()

        and: "an update command"
        def command = new UpdateIncomeCommand(
            incomeId,
            "750.00",
            "GBP",
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )

        when: "updating the income"
        service.update(command)

        then: "it throws IllegalStateException"
        def exception = thrown(IllegalStateException)
        exception.message == "Income not found: " + incomeId
    }

    def "should mark income as paid"() {
        given: "an existing income with PENDING status"
        def incomeId = IncomeId.generate()
        def existingIncome = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Payment to mark as paid",
            LocalDate.now()
        )

        and: "repository returns the existing income"
        incomeRepository.findById(incomeId) >> Optional.of(existingIncome)

        when: "marking as paid"
        service.markAsPaid(incomeId)

        then: "the income is saved with PAID status"
        1 * incomeRepository.save(_) >> { Income income ->
            assert income.id() == incomeId
            assert income.status() == PaymentStatus.PAID
            return income
        }
    }

    def "should throw exception when marking non-existent income as paid"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        and: "repository returns empty"
        incomeRepository.findById(incomeId) >> Optional.empty()

        when: "marking as paid"
        service.markAsPaid(incomeId)

        then: "it throws IllegalStateException"
        def exception = thrown(IllegalStateException)
        exception.message == "Income not found: " + incomeId
    }

    def "should mark income as overdue"() {
        given: "an existing income with PENDING status"
        def incomeId = IncomeId.generate()
        def existingIncome = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Payment to mark as overdue",
            LocalDate.now()
        )

        and: "repository returns the existing income"
        incomeRepository.findById(incomeId) >> Optional.of(existingIncome)

        when: "marking as overdue"
        service.markAsOverdue(incomeId)

        then: "the income is saved with OVERDUE status"
        1 * incomeRepository.save(_) >> { Income income ->
            assert income.id() == incomeId
            assert income.status() == PaymentStatus.OVERDUE
            return income
        }
    }

    def "should cancel income"() {
        given: "an existing income"
        def incomeId = IncomeId.generate()
        def existingIncome = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Payment to cancel",
            LocalDate.now()
        )

        and: "repository returns the existing income"
        incomeRepository.findById(incomeId) >> Optional.of(existingIncome)

        when: "cancelling the income"
        service.cancel(incomeId)

        then: "the income is saved with CANCELLED status"
        1 * incomeRepository.save(_) >> { Income income ->
            assert income.id() == incomeId
            assert income.status() == PaymentStatus.CANCELLED
            return income
        }
    }

    def "should find income by ID when exists"() {
        given: "an existing income ID"
        def incomeId = IncomeId.generate()
        def existingIncome = Income.record(
            incomeId,
            UserId.generate(),
            EventId.generate(),
            Money.gbp("500.00"),
            "Website design project",
            LocalDate.of(2026, 1, 15)
        )

        and: "repository returns the income"
        incomeRepository.findById(incomeId) >> Optional.of(existingIncome)

        when: "finding the income by ID"
        def result = service.findById(incomeId)

        then: "the income is returned"
        result.isPresent()
        result.get() == existingIncome
    }

    def "should return empty when income not found"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        and: "repository returns empty"
        incomeRepository.findById(incomeId) >> Optional.empty()

        when: "finding the income by ID"
        def result = service.findById(incomeId)

        then: "empty is returned"
        result.isEmpty()
    }

    def "should reject null income ID when finding"() {
        when: "finding with null ID"
        service.findById(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Income ID cannot be null"
    }

    def "should check if income exists"() {
        given: "an existing income ID"
        def incomeId = IncomeId.generate()

        and: "repository confirms existence"
        incomeRepository.existsById(incomeId) >> true

        when: "checking if income exists"
        def exists = service.existsById(incomeId)

        then: "true is returned"
        exists
    }

    def "should return false when income does not exist"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        and: "repository returns false"
        incomeRepository.existsById(incomeId) >> false

        when: "checking if income exists"
        def exists = service.existsById(incomeId)

        then: "false is returned"
        !exists
    }

    def "should reject null income ID when checking existence"() {
        when: "checking existence with null ID"
        service.existsById(null)

        then: "it throws IllegalArgumentException"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Income ID cannot be null"
    }
}
