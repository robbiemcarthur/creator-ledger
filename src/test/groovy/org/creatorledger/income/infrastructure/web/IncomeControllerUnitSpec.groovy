package org.creatorledger.income.infrastructure.web

import org.creatorledger.common.Money
import org.creatorledger.event.api.EventId
import org.creatorledger.income.api.IncomeId
import org.creatorledger.income.application.IncomeApplicationService
import org.creatorledger.income.domain.Income
import org.creatorledger.income.api.PaymentStatus
import org.creatorledger.user.api.UserId
import org.springframework.http.HttpStatus
import spock.lang.Specification
import java.time.LocalDate

class IncomeControllerUnitSpec extends Specification {

    IncomeApplicationService incomeApplicationService
    IncomeController controller

    def setup() {
        incomeApplicationService = Mock(IncomeApplicationService)
        controller = new IncomeController(incomeApplicationService)
    }

    def "should record new income"() {
        given: "a record income request"
        def request = new RecordIncomeRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "500.00",
            "GBP",
            "Website design project",
            LocalDate.of(2026, 1, 15)
        )

        and: "the application service will return an income ID"
        def incomeId = IncomeId.generate()
        incomeApplicationService.record(_) >> incomeId

        when: "recording the income"
        def response = controller.record(request)

        then: "the response is 201 Created with location header"
        response.statusCode == HttpStatus.CREATED
        response.headers.getLocation().toString() == "/api/income/${incomeId.value()}"
    }

    def "should return 400 when recording fails with invalid data"() {
        given: "an invalid request"
        def request = new RecordIncomeRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "invalid",
            "GBP",
            "Project",
            LocalDate.of(2026, 1, 15)
        )

        and: "the application service throws exception"
        incomeApplicationService.record(_) >> { throw new IllegalArgumentException("Invalid amount") }

        when: "recording the income"
        def response = controller.record(request)

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should update existing income"() {
        given: "an update income request"
        def incomeId = IncomeId.generate()
        def request = new UpdateIncomeRequest(
            "750.00",
            "GBP",
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )

        when: "updating the income"
        def response = controller.update(incomeId.value().toString(), request)

        then: "the application service is called"
        1 * incomeApplicationService.update(_)

        and: "the response is 204 No Content"
        response.statusCode == HttpStatus.NO_CONTENT
    }

    def "should return 400 when update fails"() {
        given: "an update request"
        def incomeId = IncomeId.generate()
        def request = new UpdateIncomeRequest(
            "750.00",
            "GBP",
            "Updated description",
            LocalDate.of(2026, 2, 20)
        )

        and: "the application service throws exception"
        incomeApplicationService.update(_) >> { throw new IllegalStateException("Income not found") }

        when: "updating the income"
        def response = controller.update(incomeId.value().toString(), request)

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should get income by ID"() {
        given: "an existing income"
        def incomeId = IncomeId.generate()
        def userId = UserId.generate()
        def eventId = EventId.generate()
        def income = Income.record(
            incomeId,
            userId,
            eventId,
            Money.gbp("500.00"),
            "Website design project",
            LocalDate.of(2026, 1, 15)
        )

        and: "the application service returns the income"
        incomeApplicationService.findById(incomeId) >> Optional.of(income)

        when: "getting the income"
        def response = controller.getIncome(incomeId.value().toString())

        then: "the response is 200 OK with income data"
        response.statusCode == HttpStatus.OK
        response.body.id() == incomeId.value().toString()
        response.body.userId() == userId.value().toString()
        response.body.eventId() == eventId.value().toString()
        response.body.amount() == "500.00"
        response.body.currency() == "GBP"
        response.body.description() == "Website design project"
        response.body.receivedDate() == LocalDate.of(2026, 1, 15).toString()
        response.body.status() == "PENDING"
    }

    def "should return 404 when income not found"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        and: "the application service returns empty"
        incomeApplicationService.findById(incomeId) >> Optional.empty()

        when: "getting the income"
        def response = controller.getIncome(incomeId.value().toString())

        then: "the response is 404 Not Found"
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return 400 for invalid UUID format"() {
        when: "getting income with invalid UUID"
        def response = controller.getIncome("invalid-uuid")

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should check if income exists"() {
        given: "an existing income ID"
        def incomeId = IncomeId.generate()

        and: "the application service confirms existence"
        incomeApplicationService.existsById(incomeId) >> true

        when: "checking existence"
        def response = controller.existsById(incomeId.value().toString())

        then: "the response is 200 OK with true"
        response.statusCode == HttpStatus.OK
        response.body == true
    }

    def "should return false when income does not exist"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        and: "the application service returns false"
        incomeApplicationService.existsById(incomeId) >> false

        when: "checking existence"
        def response = controller.existsById(incomeId.value().toString())

        then: "the response is 200 OK with false"
        response.statusCode == HttpStatus.OK
        response.body == false
    }

    def "should mark income as paid"() {
        given: "an existing income ID"
        def incomeId = IncomeId.generate()

        when: "marking as paid"
        def response = controller.markAsPaid(incomeId.value().toString())

        then: "the application service is called"
        1 * incomeApplicationService.markAsPaid(incomeId)

        and: "the response is 204 No Content"
        response.statusCode == HttpStatus.NO_CONTENT
    }

    def "should return 400 when mark as paid fails"() {
        given: "a non-existent income ID"
        def incomeId = IncomeId.generate()

        and: "the application service throws exception"
        incomeApplicationService.markAsPaid(_) >> { throw new IllegalStateException("Income not found") }

        when: "marking as paid"
        def response = controller.markAsPaid(incomeId.value().toString())

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should mark income as overdue"() {
        given: "an existing income ID"
        def incomeId = IncomeId.generate()

        when: "marking as overdue"
        def response = controller.markAsOverdue(incomeId.value().toString())

        then: "the application service is called"
        1 * incomeApplicationService.markAsOverdue(incomeId)

        and: "the response is 204 No Content"
        response.statusCode == HttpStatus.NO_CONTENT
    }

    def "should cancel income"() {
        given: "an existing income ID"
        def incomeId = IncomeId.generate()

        when: "cancelling the income"
        def response = controller.cancel(incomeId.value().toString())

        then: "the application service is called"
        1 * incomeApplicationService.cancel(incomeId)

        and: "the response is 204 No Content"
        response.statusCode == HttpStatus.NO_CONTENT
    }
}
