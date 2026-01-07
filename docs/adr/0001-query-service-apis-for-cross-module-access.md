# ADR 0001: Query Service APIs for Cross-Module Data Access

## Status

Accepted

## Context

CreatorLedger is built as a modular monolith using Spring Modulith, which enforces strict module boundaries to maintain architectural integrity. When implementing the Reporting module's TaxYearSummaryApplicationService, we needed to access income and expense data from other modules.

The initial implementation directly injected `IncomeRepository` and `ExpenseRepository` from the internal application packages of the Income and Expense modules. This violated Spring Modulith's boundary rules, as repositories are internal implementation details and should not be exposed across module boundaries.

Spring Modulith detected these violations:
- Module 'reporting' depends on module 'income' via internal package access
- Module 'reporting' depends on module 'expense' via internal package access

We needed a solution that:
1. Respects Spring Modulith module boundaries
2. Follows Domain-Driven Design's Published Language pattern
3. Prevents exposing internal domain objects across modules
4. Maintains clean separation of concerns
5. Enables proper testing and mocking

## Decision

We will use **Query Service APIs with Data Transfer Objects (DTOs)** for all cross-module read operations.

### Implementation Pattern

For each module that needs to expose data to other modules:

1. **Define a query service interface in the module's API package** (e.g., `org.creatorledger.income.api.IncomeQueryService`)
   - This is part of the module's Published Language
   - Only exposes methods needed by external consumers
   - Declares dependencies in `@ApplicationModule` annotation

2. **Create DTOs in the API package** (e.g., `IncomeData`, `ExpenseData`)
   - Immutable records containing only the data needed for cross-module communication
   - Prevents exposing internal domain objects
   - Provides a stable contract that can evolve independently

3. **Implement the query service in the module's application layer**
   - Uses internal repositories to fetch domain objects
   - Converts domain objects to DTOs before returning
   - Maintains encapsulation of internal domain logic

4. **Consumer modules depend only on the API packages**
   - Inject query service interfaces
   - Work with DTOs, not domain objects
   - Declare allowed dependencies in `@ApplicationModule` annotation

### Example

```java
// Income Module API (Published Language)
package org.creatorledger.income.api;

public interface IncomeQueryService {
    List<IncomeData> findByUserIdAndDateRange(UserId userId, LocalDate startDate, LocalDate endDate);
}

public record IncomeData(
    IncomeId id, UserId userId, EventId eventId,
    Money amount, String description,
    LocalDate receivedDate, PaymentStatus status
) {
    public static IncomeData from(Income income) {
        return new IncomeData(
            income.id(), income.userId(), income.eventId(),
            income.amount(), income.description(),
            income.receivedDate(), income.status()
        );
    }
}

// Income Module Application Layer
package org.creatorledger.income.application;

@Service
public class DefaultIncomeQueryService implements IncomeQueryService {
    private final IncomeRepository incomeRepository;

    @Override
    public List<IncomeData> findByUserIdAndDateRange(UserId userId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(IncomeData::from)
                .toList();
    }
}

// Reporting Module (Consumer)
package org.creatorledger.reporting;

@ApplicationModule(
    displayName = "Reporting",
    allowedDependencies = {"common", "user :: api", "income :: api", "expense :: api"}
)

package org.creatorledger.reporting.application;

@Service
public class TaxYearSummaryApplicationService {
    private final IncomeQueryService incomeQueryService;
    private final ExpenseQueryService expenseQueryService;

    public TaxYearSummaryId generate(GenerateTaxYearSummaryCommand command) {
        List<IncomeData> incomes = incomeQueryService.findByUserIdAndDateRange(
            command.userId(), command.taxYear().startDate(), command.taxYear().endDate()
        );
        // Process DTOs to generate summary...
    }
}
```

## Consequences

### Positive

1. **Spring Modulith Compliance**: Module boundary rules are enforced automatically by the framework
2. **Explicit Published Language**: API packages clearly define what each module exposes
3. **Encapsulation**: Internal domain objects and repositories remain private to their modules
4. **Stable Contracts**: DTOs can evolve independently from internal domain models
5. **Better Testing**: Services can be mocked at the API interface level
6. **Clear Dependencies**: `@ApplicationModule` annotation explicitly declares what each module depends on
7. **Decoupling**: Changes to internal domain models don't automatically affect consumers
8. **Documentation**: API packages serve as living documentation of inter-module contracts

### Negative

1. **Boilerplate**: Requires creating DTO classes and conversion logic
2. **Duplication**: DTO structure may closely mirror domain objects initially
3. **Maintenance**: Changes to exposed data require updating DTOs and conversion methods
4. **Performance**: Minor overhead from DTO conversion (negligible in practice)

### Trade-offs Accepted

We accept the additional code required for DTOs and conversion logic in exchange for:
- Strong architectural boundaries
- Reduced coupling between modules
- Ability to evolve modules independently
- Compliance with Spring Modulith and DDD best practices

## Alternatives Considered

### 1. Direct Repository Access Across Modules
**Rejected**: Violates Spring Modulith boundaries and couples modules too tightly

### 2. Exposing Domain Objects Directly
**Rejected**: Breaks encapsulation and creates tight coupling to internal domain models

### 3. Domain Events for All Queries
**Rejected**: Events are for notifications of state changes, not for data queries. This would be an architectural anti-pattern.

### 4. Shared Database Access
**Rejected**: Bypasses application layer entirely, violates DDD principles, and creates database coupling

## References

- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
- [Domain-Driven Design: Published Language](https://martinfowler.com/bliki/PublishedLanguage.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- Project: `docs/SPRING-MODULITH.md`
- Project: `CLAUDE.md` - Architecture Rules Section
