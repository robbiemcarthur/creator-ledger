package org.creatorledger.reporting.infrastructure.web

import org.creatorledger.common.Money
import org.creatorledger.expense.api.ExpenseCategory
import org.creatorledger.reporting.api.TaxYearSummaryId
import org.creatorledger.reporting.application.TaxYearSummaryApplicationService
import org.creatorledger.reporting.domain.CategoryTotals
import org.creatorledger.reporting.domain.TaxYear
import org.creatorledger.reporting.domain.TaxYearSummary
import org.creatorledger.user.api.UserId
import org.springframework.http.HttpStatus
import spock.lang.Specification

class TaxYearSummaryControllerUnitSpec extends Specification {

    TaxYearSummaryApplicationService taxYearSummaryApplicationService
    TaxYearSummaryController controller

    def setup() {
        taxYearSummaryApplicationService = Mock(TaxYearSummaryApplicationService)
        controller = new TaxYearSummaryController(taxYearSummaryApplicationService)
    }

    def "should generate new tax year summary"() {
        given: "a generate request"
        def userId = UUID.randomUUID()
        def request = new GenerateTaxYearSummaryRequest(userId, 2025)

        and: "the application service will return a summary ID"
        def summaryId = TaxYearSummaryId.generate()
        taxYearSummaryApplicationService.generate(_) >> summaryId

        when: "generating the summary"
        def response = controller.generate(request)

        then: "the response is 201 Created with location header"
        response.statusCode == HttpStatus.CREATED
        response.headers.getLocation().toString() == "/api/tax-year-summaries/${summaryId.value()}"
    }

    def "should return 400 when generate fails with invalid data"() {
        given: "an invalid request"
        def request = new GenerateTaxYearSummaryRequest(UUID.randomUUID(), 1900)

        and: "the application service throws exception"
        taxYearSummaryApplicationService.generate(_) >> { throw new IllegalArgumentException("Invalid tax year") }

        when: "generating the summary"
        def response = controller.generate(request)

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should retrieve tax year summary by ID"() {
        given: "an existing summary"
        def summaryId = TaxYearSummaryId.generate()
        def userId = UserId.generate()
        def taxYear = TaxYear.of(2025)
        def categoryTotals = CategoryTotals.of([
            (ExpenseCategory.EQUIPMENT): Money.gbp("500.00"),
            (ExpenseCategory.SOFTWARE): Money.gbp("200.00")
        ])
        def summary = TaxYearSummary.generate(
            summaryId,
            userId,
            taxYear,
            Money.gbp("5000.00"),
            Money.gbp("700.00"),
            categoryTotals
        )

        and: "the application service returns it"
        taxYearSummaryApplicationService.findById(summaryId) >> Optional.of(summary)

        when: "retrieving the summary"
        def response = controller.getSummary(summaryId.value().toString())

        then: "the response is 200 OK with summary data"
        response.statusCode == HttpStatus.OK
        def body = response.body
        body.id() == summaryId.value().toString()
        body.userId() == userId.value().toString()
        body.taxYear() == 2025
        body.totalIncome().amount() == "5000.00"
        body.totalIncome().currency() == "GBP"
        body.totalExpenses().amount() == "700.00"
        body.totalExpenses().currency() == "GBP"
        body.profit().amount() == "4300.00"
        body.categoryTotals().size() == 2
        body.categoryTotals()[ExpenseCategory.EQUIPMENT].amount() == "500.00"
        body.categoryTotals()[ExpenseCategory.SOFTWARE].amount() == "200.00"
    }

    def "should return 404 when summary not found"() {
        given: "a non-existent summary ID"
        def summaryId = TaxYearSummaryId.generate()

        and: "the application service returns empty"
        taxYearSummaryApplicationService.findById(summaryId) >> Optional.empty()

        when: "retrieving the summary"
        def response = controller.getSummary(summaryId.value().toString())

        then: "the response is 404 Not Found"
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return 400 when ID is invalid UUID"() {
        when: "retrieving with invalid ID"
        def response = controller.getSummary("not-a-uuid")

        then: "the response is 400 Bad Request"
        response.statusCode == HttpStatus.BAD_REQUEST
    }
}
