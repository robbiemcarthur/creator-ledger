# Spring Modulith in CreatorLedger

This document explains how Spring Modulith is used in CreatorLedger to enforce modular architecture.

## What is Spring Modulith?

**Spring Modulith** helps build well-structured Spring Boot applications with:
- **Verified module boundaries** - Prevents unwanted dependencies between modules
- **Event-driven communication** - Modules communicate via domain events
- **Automated testing** - Verifies architecture rules
- **Documentation generation** - Creates module diagrams automatically

## Module Structure

Spring Modulith recognizes modules based on **package structure** under the main application package:

```
org.creatorledger/              ← Application root
├── CreatorLedgerApplication    ← Spring Boot main class
├── user/                       ← User module
│   ├── domain/                 ← Domain model (internal)
│   ├── application/            ← Application services (internal)
│   └── infrastructure/         ← Adapters (internal)
├── event/                      ← Event module
├── income/                     ← Income module
├── expense/                    ← Expense module
├── reporting/                  ← Reporting module
└── common/                     ← Common module (shared)
```

## Module Boundaries

### Default Rules (No Configuration Needed)

Spring Modulith enforces these rules **automatically**:

1. **Modules can only access other modules through their public API**
   - ❌ `income` module cannot directly import `event.domain.Event`
   - ✅ `income` module CAN subscribe to `event` module's `EventCreated` event

2. **Internal packages are hidden**
   - Anything in `domain/`, `application/`, `infrastructure/` is **internal**
   - Only top-level classes in the module package are **public**

3. **Named interfaces define public API**
   - Create a package like `user/api/` to explicitly expose public interfaces
   - Everything else is internal by default

## How Modules Communicate

### 1. Domain Events (Asynchronous)

Modules publish and listen to events **without direct dependencies**:

```java
// User module publishes event
public record UserRegistered(UserId userId, Email email, Instant occurredAt) {}

// Income module listens to event (different module!)
@ApplicationModuleListener
void on(UserRegistered event) {
    // Handle user registration in income module
}
```

**Key Point**: The `income` module doesn't import anything from `user` module!

### 2. Shared Value Objects

Modules can share **immutable value objects** from the `common` module:

```java
// Common module
public record Money(BigDecimal amount, String currency) {}
public record UserId(UUID value) {}

// Used in User, Income, Expense modules without coupling
```

### 3. Application Service Queries (Synchronous)

The `reporting` module needs data from other modules:

```java
// Reporting module queries Income module via interface
public interface IncomeQueryService {  // Public API in income/api/
    Money totalIncomeForTaxYear(UserId userId, TaxYear taxYear);
}
```

## Verifying Module Structure

### Architecture Tests

Spring Modulith provides automated tests to verify your module structure:

```groovy
@SpringBootTest
class ModularitySpec extends Specification {

    @Autowired
    ApplicationModules modules

    def "should have valid module structure"() {
        expect: "all modules are well-formed"
        modules.verify()
    }

    def "should document module structure"() {
        when: "generating documentation"
        new Documenter(modules).writeDocumentation()

        then: "documentation is created"
        // Creates diagrams in target/modulith-docs/
    }
}
```

**This test will FAIL if**:
- Module A directly imports from module B's internal packages
- Circular dependencies exist
- Module boundaries are violated

## Event Publication

### Publishing Events

Use Spring's `ApplicationEventPublisher`:

```java
@Service
public class UserApplicationService {
    private final ApplicationEventPublisher eventPublisher;

    public UserId register(RegisterUserCommand command) {
        User user = User.register(command.email());
        userRepository.save(user);

        // Publish domain event
        eventPublisher.publishEvent(
            new UserRegistered(user.id(), user.email(), Instant.now())
        );

        return user.id();
    }
}
```

### Listening to Events

Use `@ApplicationModuleListener` for **cross-module** event handling:

```java
// In Income module
@Service
class IncomeEventListener {

    @ApplicationModuleListener
    void on(EventCreated event) {
        // Handle event created in income module
        // This runs asynchronously and in a separate transaction
    }
}
```

**@ApplicationModuleListener vs @EventListener**:
- `@ApplicationModuleListener` - Cross-module, async, transactional, persisted
- `@EventListener` - Same module, sync, same transaction

## Event Publication Registry

Spring Modulith can persist events for:
- **Reliability**: Events aren't lost if listener fails
- **Replay**: Re-process events if needed
- **Auditing**: Track all events in the system

Enable with:
```yaml
spring:
  modulith:
    events:
      jdbc:
        enabled: true
```

This creates an `EVENT_PUBLICATION` table tracking all published events.

## Module Documentation

Generate module diagrams automatically:

```java
ApplicationModules modules = ApplicationModules.of(CreatorLedgerApplication.class);
new Documenter(modules)
    .writeModulesAsPlantUml()
    .writeIndividualModulesAsPlantUml();
```

Creates:
- `target/modulith-docs/components.puml` - Component diagram
- `target/modulith-docs/module-user.puml` - Individual module diagrams

## Configuration in CreatorLedger

### Current Setup (Minimal)

```gradle
// build.gradle
implementation "org.springframework.modulith:spring-modulith-starter-core:${springModulithVersion}"
testImplementation "org.springframework.modulith:spring-modulith-starter-test:${springModulithVersion}"
```

### What We Need to Add

1. **Module API packages** (optional but recommended):
   ```
   user/
   ├── api/                    ← Public API (exposed)
   │   └── UserQueryService
   ├── domain/                 ← Internal
   ├── application/            ← Internal
   └── infrastructure/         ← Internal
   ```

2. **Event listeners** with `@ApplicationModuleListener`

3. **Event publication** in application services

4. **Architecture verification tests**

5. **Event publication registry** (optional, for reliability)

## Benefits We Get

### 1. Compile-Time Safety ❌ (Not Yet)
Spring Modulith verifies at **test time**, not compile time:
- Run tests → Modulith checks module boundaries
- Violations cause test failures
- But code still compiles!

### 2. Runtime Enforcement ✅
- Events are published asynchronously
- Listeners run in separate transactions
- Module boundaries are respected

### 3. Documentation ✅
- Auto-generated module diagrams
- Shows dependencies between modules
- Keeps architecture visible

### 4. Evolvability ✅
- Easy to extract modules into microservices later
- Module boundaries are already well-defined
- Event-driven communication is microservice-ready

## Example: Full Event Flow

### Step 1: User registers (User Module)
```java
// UserApplicationService
eventPublisher.publishEvent(new UserRegistered(userId, email, Instant.now()));
```

### Step 2: Event is persisted (Spring Modulith)
```sql
INSERT INTO EVENT_PUBLICATION (id, event_type, serialized_event, publication_date)
VALUES (...);
```

### Step 3: Income module handles event (Income Module)
```java
@ApplicationModuleListener
void on(UserRegistered event) {
    // Create default income settings for new user
    createDefaultIncomeSettings(event.userId());
}
```

### Step 4: Event is marked complete (Spring Modulith)
```sql
UPDATE EVENT_PUBLICATION SET completed_date = NOW() WHERE id = ?;
```

## Common Pitfalls

### ❌ Don't Do This
```java
// Income module directly importing User domain object
import org.creatorledger.user.domain.User;  // WRONG!

public class Income {
    private User user;  // Tight coupling!
}
```

### ✅ Do This Instead
```java
// Income module only references UserId (shared value object)
import org.creatorledger.common.domain.UserId;  // Correct!

public record Income(UserId userId, ...) {}
```

## Testing Strategy

### Unit Tests
- Test individual modules in isolation
- Mock dependencies
- No Spring Modulith needed

### Integration Tests
- Test module boundaries
- Use `ApplicationModules.verify()`
- Test event publishing/listening

### Architecture Tests
```groovy
def "should not have circular dependencies"() {
    expect: "no circular dependencies between modules"
    modules.verify()
}

def "should respect module boundaries"() {
    expect: "income module doesn't directly depend on event module"
    !modules.getModuleByName("income")
           .getDependencies(modules)
           .contains(modules.getModuleByName("event"))
}
```

## Next Steps for CreatorLedger

To fully leverage Spring Modulith, we need to:

1. ✅ **Module structure** - Already in place
2. ❌ **Add event publishers** - Publish domain events in application services
3. ❌ **Add event listeners** - Use `@ApplicationModuleListener` for cross-module communication
4. ❌ **Add module tests** - Verify architecture with `ApplicationModules.verify()`
5. ❌ **Enable event publication registry** - For reliability and auditing
6. ❌ **Generate documentation** - Auto-generate module diagrams

## Resources

- [Spring Modulith Reference](https://docs.spring.io/spring-modulith/reference/)
- [Spring Modulith Samples](https://github.com/spring-projects/spring-modulith/tree/main/spring-modulith-examples)
- [Baeldung Tutorial](https://www.baeldung.com/spring-modulith)

## Summary

**Spring Modulith is a framework, not magic!** It needs:
- Proper package structure (✅ we have this)
- Event publishing/listening code (❌ we need to add this)
- Architecture tests (❌ we need to add this)

Once we add the missing pieces, Spring Modulith will:
- Enforce module boundaries at test time
- Provide reliable event-driven communication
- Generate architectural documentation
- Make our modular monolith production-ready
