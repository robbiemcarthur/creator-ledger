# Container Diagram

Shows the high-level architecture of CreatorLedger using Spring Modulith.

```mermaid
C4Container
    title Container Diagram - CreatorLedger

    Person(creator, "Self-Employed Creative", "Tracks financial activity")

    System_Boundary(creatorLedger, "CreatorLedger") {
        Container(webApp, "Web Application", "Spring Boot, React", "Provides UI for managing events, income, expenses, and viewing reports")

        Container_Boundary(modulith, "Spring Modulith Application") {
            Container(userModule, "User Module", "Spring Boot", "Manages user registration and authentication")
            Container(eventModule, "Event Module", "Spring Boot", "Tracks events (gigs, projects)")
            Container(incomeModule, "Income Module", "Spring Boot", "Records and tracks income")
            Container(expenseModule, "Expense Module", "Spring Boot", "Records and categorizes expenses")
            Container(reportingModule, "Reporting Module", "Spring Boot", "Generates tax year summaries")
            Container(commonModule, "Common Module", "Spring Boot", "Shared domain objects (Money)")
        }

        ContainerDb(database, "Database", "PostgreSQL", "Stores users, events, income, expenses, and summaries")
    }

    System_Ext(hmrc, "HMRC Systems", "Tax authority")

    Rel(creator, webApp, "Uses", "HTTPS")
    Rel(webApp, userModule, "Makes API calls to", "JSON/HTTPS")
    Rel(webApp, eventModule, "Makes API calls to", "JSON/HTTPS")
    Rel(webApp, incomeModule, "Makes API calls to", "JSON/HTTPS")
    Rel(webApp, expenseModule, "Makes API calls to", "JSON/HTTPS")
    Rel(webApp, reportingModule, "Makes API calls to", "JSON/HTTPS")

    Rel(userModule, database, "Reads/writes user data", "JDBC")
    Rel(eventModule, database, "Reads/writes event data", "JDBC")
    Rel(incomeModule, database, "Reads/writes income data", "JDBC")
    Rel(expenseModule, database, "Reads/writes expense data", "JDBC")
    Rel(reportingModule, database, "Reads/writes summary data", "JDBC")

    Rel(incomeModule, eventModule, "Subscribes to EventCreated", "Domain Events")
    Rel(expenseModule, userModule, "References UserId", "Value Object")
    Rel(reportingModule, incomeModule, "Queries income totals", "Application Service")
    Rel(reportingModule, expenseModule, "Queries expense totals", "Application Service")

    Rel(reportingModule, hmrc, "Exports tax data", "CSV/JSON")

    UpdateRelStyle(incomeModule, eventModule, $offsetY="-40", $offsetX="-50")
    UpdateRelStyle(reportingModule, incomeModule, $offsetY="20")
    UpdateRelStyle(reportingModule, expenseModule, $offsetY="20")
```

## Key Containers

### Web Application (Future)
- **Technology**: Spring Boot + React
- **Purpose**: User interface for all functionality
- **Status**: Not yet implemented

### Spring Modulith Application (Current)
A modular monolith containing 6 bounded contexts:

1. **User Module** ✅ Implemented
   - User registration and management
   - REST API: `/api/users`

2. **Event Module** ✅ Domain implemented
   - Event tracking (gigs, projects, commissions)
   - Links income to specific events

3. **Income Module** ✅ Domain implemented
   - Income recording and payment tracking
   - Payment statuses (PENDING, PAID, OVERDUE, CANCELLED)

4. **Expense Module** ✅ Domain implemented
   - Expense recording and categorization
   - HMRC-aligned categories

5. **Reporting Module** ✅ Domain implemented
   - Tax year summary generation
   - Profit/loss calculations
   - Category breakdowns

6. **Common Module** ✅ Implemented
   - Shared domain objects (Money)
   - Cross-cutting concerns

### Database
- **Technology**: PostgreSQL 16
- **Purpose**: Persistent storage for all aggregates
- **Status**: JPA entities and repositories implemented for User module

## Module Communication

### Domain Events
Modules communicate asynchronously via domain events:
- `UserRegistered` - Published by User module
- `EventCreated` - Published by Event module
- `IncomeRecorded` - Published by Income module
- `ExpenseRecorded` - Published by Expense module
- `TaxYearSummaryGenerated` - Published by Reporting module

### Application Services
Reporting module queries other modules via application services to aggregate data.

### Value Object Sharing
Common module provides shared value objects (Money, UserId, etc.).

## Technology Stack

- **Framework**: Spring Boot 4.0
- **Modulith**: Spring Modulith 1.2.4
- **Language**: Java 21
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA / Hibernate
- **Testing**: Spock (Groovy), Testcontainers
- **Build**: Gradle 9.2

## Deployment

- **Current**: Single JAR deployment (modular monolith)
- **Future**: Could split into microservices if needed (Spring Modulith supports this)
- **Target**: Oracle Cloud Infrastructure (Docker container)

## Architecture Patterns

- **Hexagonal Architecture**: Each module follows ports & adapters
- **Domain-Driven Design**: Clear bounded contexts with aggregates, entities, value objects
- **Event-Driven**: Asynchronous communication via domain events
- **CQRS-lite**: Separation of commands and queries (not full CQRS)
