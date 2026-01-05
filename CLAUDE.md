# CLAUDE.md

This file provides strict guidance to Claude Code (claude.ai/code) when generating, modifying, or reviewing code in this repository. Claude must follow these rules exactly.

# 1. Project Overview

CreatorLedger is a modular, domain‑driven financial organiser for UK self‑employed creatives. The system models events, income, expenses, and tax‑year summaries using:

- Spring Boot
- Spring Modulith
- Domain‑Driven Design
- Event‑driven communication
- Spock BDD testing
- TDD + Tidy‑First workflow
- Clean, maintainable code

# 2. How Claude Must Work

## 2.1 TDD First, Always
Claude must:
- Write a failing Spock test first
- Use BDD narratives (given/when/then)
- Implement minimum code to pass
- Refactor only when green
- Keep structural and behavioural changes separate

## 2.2 Follow my Coding Standards: CODINGSTANDARDS.md whenever you write Java code. 

### Commit discipline
Claude must only commit when:
- All tests pass
- No warnings
- Single logical change
- Commit message clearly indicates refactor or feat

# 3. Architecture Rules

## 3.1 Modular Monolith (Spring Modulith)
The system is composed of modules, each representing a bounded context. Modules communicate only via domain events.

Claude must:
- Keep module internals package‑private
- Expose only application‑level APIs
- Use @ApplicationModuleListener for inter‑module events
- Never import domain classes across modules

## 3.2 Bounded Contexts

### User Module (user)
- Aggregate: User
- Value Objects: UserId, Email
- Events: UserRegistered

### Event Module (event)
- Aggregate: Event
- Value Objects: EventId, EventDate, ClientName
- Events: EventCreated, EventUpdated

### Income Module (income)
- Aggregate: Income
- Value Objects: IncomeId, Money, PaymentStatus
- Events: IncomeRecorded

### Expense Module (expense)
- Aggregate: Expense
- Value Objects: ExpenseId, Money, ExpenseCategory
- Events: ExpenseRecorded

### Reporting Module (reporting)
- Aggregate: TaxYearSummary
- Value Objects: TaxYear, CategoryTotals
- Events: TaxYearSummaryGenerated

# 4. Domain Modeling Rules

Claude must:
- Prefer value objects over primitives
- Use records for immutable VOs
- Use static factories instead of constructors
- Avoid setters
- Keep aggregates small and consistent
- Publish domain events inside aggregates
- Avoid leaking persistence concerns into the domain

### Forbidden in domain layer
- Spring annotations
- JPA annotations
- Lombok (unless explicitly allowed)
- External service calls
- Logging

# 5. Event‑Driven Rules

Claude must:
- Use domain events for module communication
- Keep domain events separate from integration events
- Use Spring events for in‑process communication
- Use Kafka only in infrastructure layer (future)
- Never block on event publishing

# 6. Testing Rules

Claude must:
- Use Spock for all tests
- Use BDD narratives
- Write unit tests first
- Use Testcontainers for integration tests
- Write contract tests for event schemas
- Name tests behaviourally (e.g., shouldCalculateProfitForTaxYear)

### Test structure example
def "should calculate profit for a tax year"() {
given: "a user with income and expenses"
...
when: "the tax year summary is generated"
...
then: "profit equals income minus expenses"
...
}

# 7. CUPID Principles

Claude must follow CUPID:
- Composable
- Unix philosophy (do one thing well)
- Predictable
- Idiomatic
- Domain‑based

# 8. Effective Java Rules

Claude must apply:
- Static factories over constructors
- Minimise mutability
- Prefer composition
- Defensive copies
- Minimise variable scope
- Avoid string concatenation in loops
- Use builders for complex aggregates

# 9. Code Quality Rules

Claude must:
- Keep methods small
- Keep classes focused
- Avoid God objects
- Avoid anemic domain models
- Avoid leaking persistence models into domain
- Use clear, intention‑revealing names

# 10. Forbidden Actions

Claude must never:
- Add behaviour without a failing test
- Mix refactoring and behaviour
- Break module boundaries
- Add annotations to domain objects
- Use .properties files
- Introduce new frameworks
- Generate controllers/services without tests
- Add persistence logic to domain layer
- Use inheritance where composition fits better

# 11. Allowed Patterns

Claude should prefer:
- Strategy (tax calculation, categorisation)
- Factory (domain event creation)
- Observer (event listeners)
- Builder (complex aggregates)
- State (future workflow states)

# 12. Configuration Rules

Claude must:
- Use application.yml only
- Use profile‑specific YAML files
- Keep configuration minimal and explicit

# 13. CI/CD Expectations

- GitHub Actions
- Build → Test → Quality → Package
- Keep pipeline fast and deterministic

# 14. Explaining Code Decisions

Claude must always explain why, referencing:
- DDD tactical patterns
- CUPID
- Effective Java
- Modulith boundaries
- Event‑driven reasoning
- Refactoring rationale

# 15. Learning Focus

This project teaches:
- DDD
- Event‑driven architecture
- Modular monolith design
- Clean code
- TDD
- Spock BDD
- CI/CD discipline

Claude must support these goals at all times.