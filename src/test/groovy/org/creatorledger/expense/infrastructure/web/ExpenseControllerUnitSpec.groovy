package org.creatorledger.expense.infrastructure.web

import org.creatorledger.common.Money
import org.creatorledger.expense.api.ExpenseCategory
import org.creatorledger.expense.api.ExpenseId
import org.creatorledger.expense.application.ExpenseApplicationService
import org.creatorledger.expense.domain.Expense
import org.creatorledger.user.api.UserId
import org.springframework.http.HttpStatus
import spock.lang.Specification

import java.time.LocalDate

class ExpenseControllerUnitSpec extends Specification {

    ExpenseApplicationService expenseApplicationService
    ExpenseController controller

    def setup() {
        expenseApplicationService = Mock(ExpenseApplicationService)
        controller = new ExpenseController(expenseApplicationService)
    }

    def "should record new expense"() {
        given: "a record expense request"
        def request = new RecordExpenseRequest(
            UUID.randomUUID(),
            "150.00",
            "GBP",
            ExpenseCategory.EQUIPMENT,
            "MacBook Pro laptop",
            LocalDate.of(2026, 1, 15)
        )

        and: "the application service will return an expense ID"
        def expenseId = ExpenseId.generate()
        expenseApplicationService.record(_) >> expenseId

        when: "recording the expense"
        def response = controller.record(request)

        then: "the response is 201 Created with location header"
        response.statusCode == HttpStatus.CREATED
        response.headers.getLocation().toString() == "/api/expenses/${expenseId.value()}"
    }

    def "should return 400 when recording fails with invalid data"() {
        given: "an invalid request"
        def request = new RecordExpenseRequest(
            UUID.randomUUID(),
            "invalid",
            "GBP",
            ExpenseCategory.EQUIPMENT,
            "Laptop",
            LocalDate.of(2026, 1, 15)
        )

        and: "the application service throws exception"
        expenseApplicationService.record(_) >> { throw new IllegalArgumentException("Invalid amount") }

        when: "recording the expense"
        def response = controller.record(request)

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should update existing expense"() {
        given: "an update expense request"
        def expenseId = ExpenseId.generate()
        def request = new UpdateExpenseRequest(
            "200.00",
            "GBP",
            ExpenseCategory.SOFTWARE,
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )

        when: "updating the expense"
        def response = controller.update(expenseId.value().toString(), request)

        then: "the application service is called"
        1 * expenseApplicationService.update(_)

        and: "the response is 204 No Content"
        response.statusCode == HttpStatus.NO_CONTENT
    }

    def "should return 400 when update fails"() {
        given: "an update request"
        def expenseId = ExpenseId.generate()
        def request = new UpdateExpenseRequest(
            "200.00",
            "GBP",
            ExpenseCategory.SOFTWARE,
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )

        and: "the application service throws exception"
        expenseApplicationService.update(_) >> { throw new IllegalStateException("Expense not found") }

        when: "updating the expense"
        def response = controller.update(expenseId.value().toString(), request)

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should get expense by ID"() {
        given: "an existing expense"
        def expenseId = ExpenseId.generate()
        def expense = Expense.record(
            expenseId,
            UserId.generate(),
            Money.gbp("150.00"),
            ExpenseCategory.EQUIPMENT,
            "MacBook Pro laptop",
            LocalDate.of(2026, 1, 15)
        )

        and: "the application service returns the expense"
        expenseApplicationService.findById(expenseId) >> Optional.of(expense)

        when: "getting the expense"
        def response = controller.getExpense(expenseId.value().toString())

        then: "the response is 200 OK with expense data"
        response.statusCode == HttpStatus.OK
        response.body.id == expenseId.value().toString()
        response.body.amount == "150.00"
        response.body.category == "EQUIPMENT"
        response.body.description == "MacBook Pro laptop"
    }

    def "should return 404 when expense not found"() {
        given: "a non-existent expense ID"
        def expenseId = ExpenseId.generate()

        and: "the application service returns empty"
        expenseApplicationService.findById(expenseId) >> Optional.empty()

        when: "getting the expense"
        def response = controller.getExpense(expenseId.value().toString())

        then: "the response is 404 Not Found"
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return 400 when getting expense with invalid ID"() {
        when: "getting expense with invalid UUID"
        def response = controller.getExpense("invalid-uuid")

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should check if expense exists"() {
        given: "an expense ID"
        def expenseId = ExpenseId.generate()

        and: "the application service returns true"
        expenseApplicationService.existsById(expenseId) >> true

        when: "checking existence"
        def response = controller.exists(expenseId.value().toString())

        then: "the response is 200 OK with true"
        response.statusCode == HttpStatus.OK
        response.body == true
    }

    def "should return false when expense does not exist"() {
        given: "a non-existent expense ID"
        def expenseId = ExpenseId.generate()

        and: "the application service returns false"
        expenseApplicationService.existsById(expenseId) >> false

        when: "checking existence"
        def response = controller.exists(expenseId.value().toString())

        then: "the response is 200 OK with false"
        response.statusCode == HttpStatus.OK
        response.body == false
    }
}
