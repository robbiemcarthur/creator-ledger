package org.creatorledger.reporting.infrastructure.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.creatorledger.event.api.EventId
import org.creatorledger.expense.api.ExpenseCategory
import org.creatorledger.expense.application.ExpenseApplicationService
import org.creatorledger.expense.application.RecordExpenseCommand
import org.creatorledger.income.application.IncomeApplicationService
import org.creatorledger.income.application.RecordIncomeCommand
import org.creatorledger.reporting.api.TaxYearSummaryId
import org.creatorledger.user.api.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header

@SpringBootTest
@Transactional
@Testcontainers
class TaxYearSummaryControllerIntegrationSpec extends Specification {

    @Shared
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

    @Autowired
    WebApplicationContext webApplicationContext

    MockMvc mockMvc
    ObjectMapper objectMapper

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        objectMapper = new ObjectMapper()
    }

    @Autowired
    IncomeApplicationService incomeApplicationService

    @Autowired
    ExpenseApplicationService expenseApplicationService

    def "should generate tax year summary via HTTP POST"() {
        given: "a user with income and expenses for tax year 2025"
        def userId = UserId.generate()

        and: "some income records"
        incomeApplicationService.record(new RecordIncomeCommand(
                userId,
                EventId.generate(),
                "1000.00",
                "GBP",
                "Project A",
                LocalDate.of(2025, 5, 1)
        ))
        incomeApplicationService.record(new RecordIncomeCommand(
                userId,
                EventId.generate(),
                "1500.00",
                "GBP",
                "Project B",
                LocalDate.of(2025, 7, 15)
        ))

        and: "some expense records"
        expenseApplicationService.record(new RecordExpenseCommand(
                userId,
                "300.00",
                "GBP",
                ExpenseCategory.EQUIPMENT,
                "Laptop",
                LocalDate.of(2025, 6, 1)
        ))
        expenseApplicationService.record(new RecordExpenseCommand(
                userId,
                "200.00",
                "GBP",
                ExpenseCategory.SOFTWARE,
                "Software license",
                LocalDate.of(2025, 8, 1)
        ))

        and: "a generate request JSON"
        def request = new GenerateTaxYearSummaryRequest(userId.value(), 2025)
        def requestJson = objectMapper.writeValueAsString(request)

        expect: "posting returns 201 Created with location header"
        mockMvc.perform(post("/api/tax-year-summaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/tax-year-summaries/")))
    }

    def "should retrieve tax year summary via HTTP GET"() {
        given: "a user with income and expenses"
        def userId = UserId.generate()

        incomeApplicationService.record(new RecordIncomeCommand(
                userId,
                EventId.generate(),
                "5000.00",
                "GBP",
                "Contract work",
                LocalDate.of(2025, 5, 1)
        ))

        expenseApplicationService.record(new RecordExpenseCommand(
                userId,
                "700.00",
                "GBP",
                ExpenseCategory.EQUIPMENT,
                "Equipment",
                LocalDate.of(2025, 6, 1)
        ))

        and: "a generated summary"
        def generateRequest = new GenerateTaxYearSummaryRequest(userId.value(), 2025)
        def requestJson = objectMapper.writeValueAsString(generateRequest)

        def result = mockMvc.perform(post("/api/tax-year-summaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()

        def location = result.response.getHeader("Location")

        expect: "retrieving returns 200 OK with correct data"
        def getResult = mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andReturn()

        def responseBody = objectMapper.readValue(getResult.response.contentAsString, TaxYearSummaryResponse)
        responseBody.userId() == userId.value().toString()
        responseBody.taxYear() == 2025
        responseBody.totalIncome().amount() == "5000.00"
        responseBody.totalIncome().currency() == "GBP"
        responseBody.totalExpenses().amount() == "700.00"
        responseBody.totalExpenses().currency() == "GBP"
        responseBody.profit().amount() == "4300.00"
        responseBody.categoryTotals()[ExpenseCategory.EQUIPMENT].amount() == "700.00"
    }

    def "should return 404 for non-existent summary"() {
        given: "a non-existent summary ID"
        def fakeId = TaxYearSummaryId.generate()

        expect: "retrieving returns 404 Not Found"
        mockMvc.perform(get("/api/tax-year-summaries/${fakeId.value()}"))
                .andExpect(status().isNotFound())
    }

    def "should return 400 for invalid tax year"() {
        given: "a request with invalid tax year"
        def request = new GenerateTaxYearSummaryRequest(UUID.randomUUID(), 1900)
        def requestJson = objectMapper.writeValueAsString(request)

        expect: "posting returns 400 Bad Request"
        mockMvc.perform(post("/api/tax-year-summaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
    }

    def "should return 400 for invalid UUID in path"() {
        expect: "retrieving with invalid UUID returns 400 Bad Request"
        mockMvc.perform(get("/api/tax-year-summaries/not-a-uuid"))
                .andExpect(status().isBadRequest())
    }
}
