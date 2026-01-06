# Hexagonal Architecture (Ports and Adapters)

This document explains how CreatorLedger implements Hexagonal Architecture, also known as Ports and Adapters pattern.

## Overview

Hexagonal Architecture achieves clean separation of concerns by defining **ports** (interfaces) that the application needs, and **adapters** (implementations) that fulfill those needs.

The key principle: **The application defines what it needs; the infrastructure provides it.**

## The Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  - Pure business logic                                       │
│  - No framework dependencies                                 │
│  - Value Objects, Entities, Domain Events                    │
└─────────────────────────────────────────────────────────────┘
                              ↓ uses
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                          │
│  - Use cases (Commands, Queries)                             │
│  - Application Services                                      │
│  - PORTS (Repository Interfaces) ← DEFINED HERE              │
└─────────────────────────────────────────────────────────────┘
                              ↑ implements
┌─────────────────────────────────────────────────────────────┐
│                  INFRASTRUCTURE LAYER                        │
│  - ADAPTERS (Repository Implementations) ← IMPLEMENTED HERE  │
│  - Framework-specific code (JPA, Spring, etc.)               │
│  - External integrations                                     │
└─────────────────────────────────────────────────────────────┘
```

## Repository Pattern Implementation

### Port (Interface) - Application Layer

**Location**: `/application/*Repository.java`

The application layer defines the **port** (interface) that specifies what persistence operations are needed.

```java
// src/main/java/org/creatorledger/user/application/UserRepository.java
package org.creatorledger.user.application;

import org.creatorledger.user.domain.User;
import org.creatorledger.user.api.UserId;
import java.util.Optional;

/**
 * Repository interface for User aggregate persistence.
 * <p>
 * This is a PORT in hexagonal architecture - it defines the contract
 * that the application layer needs for persistence, without specifying
 * HOW that persistence is implemented.
 * </p>
 */
public interface UserRepository {

    void save(User user);

    Optional<User> findById(UserId id);

    boolean existsById(UserId id);
}
```

**Why here?**
- The application layer **defines its own needs**
- Domain services depend on this interface, not on any implementation
- Follows **Dependency Inversion Principle**: high-level modules (application) don't depend on low-level modules (infrastructure)

### Adapter (Implementation) - Infrastructure Layer

**Location**: `/infrastructure/Jpa*Repository.java`

The infrastructure layer provides the **adapter** (implementation) that fulfills the port's contract using a specific technology (JPA).

```java
// src/main/java/org/creatorledger/user/infrastructure/JpaUserRepository.java
package org.creatorledger.user.infrastructure;

import org.creatorledger.user.application.UserRepository;
import org.creatorledger.user.domain.User;
import org.creatorledger.user.api.UserId;
import org.springframework.stereotype.Repository;

/**
 * JPA implementation of UserRepository.
 * <p>
 * This is an ADAPTER in hexagonal architecture - it implements the port
 * using JPA/PostgreSQL technology. The application layer has no knowledge
 * of this implementation; it only knows about the UserRepository interface.
 * </p>
 */
@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository springDataRepository;
    private final UserEntityMapper mapper;

    public JpaUserRepository(
            SpringDataUserRepository springDataRepository,
            UserEntityMapper mapper
    ) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        springDataRepository.save(entity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springDataRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UserId id) {
        return springDataRepository.existsById(id.value());
    }
}
```

**Why here?**
- This is framework-specific code (JPA, Spring annotations)
- Handles technical concerns: entity mapping, transaction management, etc.
- Can be swapped without affecting the application layer (e.g., switch to MongoDB)

## Full Module Structure

```
/user
  /api                           ← Published API (Value Objects, IDs, Events)
    UserId.java
    Email.java

  /domain                        ← Business Logic (Aggregates, Entities)
    User.java
    UserRegistered.java

  /application                   ← Use Cases & Ports
    UserApplicationService.java  ← Orchestrates use cases
    UserRepository.java          ← PORT (interface)
    RegisterUserCommand.java     ← Command pattern

  /infrastructure                ← Adapters & Framework Code
    /persistence
      JpaUserRepository.java     ← ADAPTER (JPA implementation)
      SpringDataUserRepository.java
      UserJpaEntity.java
      UserEntityMapper.java
    /web
      UserController.java        ← REST adapter
      RegisterUserRequest.java
      UserResponse.java
```

## Benefits of This Pattern

### 1. **Testability**
Application services can be tested without any infrastructure:

```groovy
def "should register new user"() {
    given:
    UserRepository mockRepo = Mock(UserRepository)  // Mock the port
    def service = new UserApplicationService(mockRepo)

    when:
    service.register(command)

    then:
    1 * mockRepo.save(_)  // Verify interaction with the port
}
```

### 2. **Flexibility**
Swap implementations without touching application code:

```java
// Current: JPA + PostgreSQL
@Repository
public class JpaUserRepository implements UserRepository { }

// Future: Switch to MongoDB
@Repository
public class MongoUserRepository implements UserRepository { }

// Application code unchanged - still depends on UserRepository interface
```

### 3. **Dependency Inversion**
- **Traditional (BAD)**: Application → JpaRepository → Database
- **Hexagonal (GOOD)**: Application → UserRepository ← JpaRepository → Database

The arrow direction matters! The application owns the interface; infrastructure implements it.

### 4. **Domain Purity**
Domain and application layers have ZERO dependencies on:
- Spring Framework
- JPA/Hibernate
- HTTP/REST
- JSON serialization
- Any infrastructure concern

## Common Module Examples

All modules in CreatorLedger follow this pattern:

| Module | Port (Interface) | Adapter (Implementation) |
|--------|------------------|--------------------------|
| User | `user/application/UserRepository.java` | `user/infrastructure/JpaUserRepository.java` |
| Event | `event/application/EventRepository.java` | `event/infrastructure/JpaEventRepository.java` |
| Income | `income/application/IncomeRepository.java` | `income/infrastructure/JpaIncomeRepository.java` |
| Expense | `expense/application/ExpenseRepository.java` | `expense/infrastructure/JpaExpenseRepository.java` |
| Reporting | `reporting/application/TaxYearSummaryRepository.java` | `reporting/infrastructure/JpaTaxYearSummaryRepository.java` |

## References

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/) - Alistair Cockburn (original)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) - Robert C. Martin
- [Dependency Inversion Principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle) - SOLID principles
- Project: `docs/SPRING-MODULITH.md` - Module boundary rules
- Project: `CLAUDE.md` - Architecture rules and patterns
