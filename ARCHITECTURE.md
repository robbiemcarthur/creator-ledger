# CreatorLedger — Architecture Overview

CreatorLedger is a modular, domain‑driven financial organiser for UK self‑employed creatives.  
The system models events, income, expenses, and tax‑year summaries using a **modular monolith** built with **Spring Modulith**, **DDD**, and **event‑driven communication**.

---

# 1. Architectural Goals

- High cohesion, low coupling
- Domain‑driven boundaries
- Event‑driven communication between modules
- Test‑first development (Spock + BDD)
- Evolvable into microservices if needed
- Clean, maintainable, predictable code (CUPID)

---

# 2. Bounded Contexts (Modules)

CreatorLedger consists of **five** bounded contexts:

## 2.1 User Module
Responsible for identity and user profile.

- Aggregate: `User`
- Value Objects: `UserId`, `Email`
- Events: `UserRegistered`

## 2.2 Event Module
Represents creative work events (gigs, shoots, commissions).

- Aggregate: `Event`
- Value Objects: `EventId`, `EventDate`, `ClientName`
- Events: `EventCreated`, `EventUpdated`

## 2.3 Income Module
Tracks income entries, optionally linked to events.

- Aggregate: `Income`
- Value Objects: `IncomeId`, `Money`, `PaymentStatus`
- Events: `IncomeRecorded`

## 2.4 Expense Module
Tracks expenses, categorised using HMRC‑aligned categories.

- Aggregate: `Expense`
- Value Objects: `ExpenseId`, `Money`, `ExpenseCategory`
- Events: `ExpenseRecorded`

## 2.5 Reporting Module
Aggregates income + expenses into tax‑year summaries.

- Aggregate: `TaxYearSummary`
- Value Objects: `TaxYear`, `CategoryTotals`
- Events: `TaxYearSummaryGenerated`

---

# 3. Module Communication

Modules communicate **only via domain events**.

- No direct imports of domain classes across modules
- Use `@ApplicationModuleListener` for event handling
- Domain events are immutable value objects

---

# 4. Layers Within Each Module

Each module follows the same structure:
<module>/ domain/ aggregate roots value objects domain events domain services application/ application services event listeners use cases infrastructure/ persistence (JPA) mappers configuration


---

# 5. Persistence

- PostgreSQL
- JPA/Hibernate in infrastructure layer only
- Domain layer contains **no** persistence annotations

---

# 6. Event-Driven Architecture

- Domain events published inside aggregates
- Spring Modulith handles in‑process event dispatch
- Future: Kafka integration for external events

---

# 7. Testing Strategy

- **Unit tests**: domain logic (Spock)
- **Integration tests**: persistence + module boundaries
- **Contract tests**: event schemas
- **Acceptance tests**: end‑to‑end flows

---

# 8. Technology Stack

- Java 21
- Spring Boot 4.x
- Spring Modulith 2.x
- Spock Framework
- PostgreSQL
- Gradle
- GitLab CI/CD

---

# 9. Future Extensions

- Calendar sync (Google)
- Mobile apps (React Native)
- Open Banking integration
- HMRC API integration
- AI‑powered categorisation