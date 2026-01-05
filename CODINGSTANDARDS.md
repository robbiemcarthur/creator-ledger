---

# **Java Coding Standards**
*A pragmatic, domain‑driven, XP‑aligned guide for maintainable backend systems*

---

# **1. Purpose & Principles**

These standards define how Java code should be written to ensure clarity, maintainability, testability, and architectural integrity. They reflect principles from:

- Domain‑Driven Design (DDD)
- Extreme Programming (XP)
- CUPID
- Effective Java
- Refactoring (Fowler)
- Modern Java idioms

The goal is to produce **simple, expressive, predictable, domain‑focused code**.

---

# **2. Core Engineering Philosophy**

## **2.1 DRY — Don’t Repeat Yourself**
- Remove duplication only when the underlying concepts are truly the same.
- Prefer duplication over premature abstraction.
- Avoid “utility” classes that collect unrelated behaviour.

## **2.2 YAGNI — You Aren’t Gonna Need It**
- Build only what is required today.
- No speculative interfaces, extension points, or abstractions.
- Avoid over‑generalising domain models.

## **2.3 KISS — Keep It Simple**
- Clarity beats cleverness.
- Prefer simple loops over complex streams.
- Avoid unnecessary patterns, layers, or indirection.

## **2.4 CUPID**
- **Composable** — small, pure functions, immutable objects
- **Unix Philosophy** — do one thing well
- **Predictable** — no hidden side effects
- **Idiomatic** — use modern Java naturally
- **Domain‑based** — domain model is the centre of the universe

## **2.5 XP Practices**
- Small, frequent commits
- Continuous refactoring
- Test‑first or test‑supported development
- Collective ownership
- Simplicity over cleverness

---

# **3. Project Structure & Architecture**

## **3.1 DDD‑Aligned Vertical Slices**
```
/feature
  /domain
  /application
  /infrastructure
```

## **3.2 Domain Layer Rules**
- Entities and value objects contain behaviour and invariants.
- No framework annotations in domain objects.
- Prefer value objects aggressively.
- Domain events model meaningful business changes.

## **3.3 Application Layer Rules**
- Contains use cases (commands, queries).
- Orchestrates domain behaviour.
- No business logic here.

## **3.4 Infrastructure Layer Rules**
- Adapters, repositories, configs.
- Framework‑specific code lives here.
- Domain must not depend on infrastructure.

---

# **4. Java Language & Syntax Style**

## **4.1 Immutability First**
- **Use `final` for all fields** unless mutation is required.
- **Use `final` for all local variables** unless mutation is intentional.
- **Never mutate method parameters.**
- Prefer immutable collections (`List.copyOf`, `Map.copyOf`).
- Prefer **records** for pure data carriers.

## **4.2 Optional Usage**
- Use `Optional<T>` only for return types.
- Never use Optional for fields, parameters, or collections.
- Avoid `get()` — use `map`, `orElse`, `orElseThrow`.

## **4.3 Modern Java Features**
- Prefer **records** for immutable data structures.
- Use **sealed classes** for controlled hierarchies.
- Use **pattern matching** for `instanceof` and `switch`.
- Use **switch expressions**.
- Use **text blocks** for multi‑line strings.
- Use **var** when the type is obvious from context.

## **4.4 Method Parameters**
- Treat parameters as immutable inputs.
- If transformation is needed, assign to a new local variable.
- Prefer passing domain objects over primitives.

## **4.5 Variable Naming & `var`**
- Use `var` only when the type is obvious.
- Avoid `var` when the type is unclear or important.
- Use descriptive names; avoid abbreviations except `id`, `url`, `dto`.

## **4.6 Collections & Streams**
- Prefer simple loops for imperative logic.
- Use streams for transformations, filtering, mapping, aggregation.
- Avoid deeply nested pipelines.
- Use `.toList()` (Java 16+) for immutable results.

---

# **5. Classes & Objects**

## **5.1 Constructors**
- Validate invariants.
- Avoid telescoping constructors — use builders when needed.
- Keep constructors simple and predictable.

## **5.2 Composition Over Inheritance**
- Inheritance only for true “is‑a” relationships.
- Prefer interfaces + composition.

## **5.3 Avoid Static State**
- Static methods are fine for pure functions.
- Static mutable state is forbidden.

---

# **6. Methods**

## **6.1 Method Size**
- Aim for ≤ 15–20 lines.
- Extract behaviour into well‑named private methods.

## **6.2 Parameters**
- Prefer domain objects over primitives.
- If >3 parameters, consider a parameter object.

## **6.3 Side Effects**
- A method should either *do something* or *return something*, not both.

---

# **7. Error Handling**

## **7.1 Exceptions**
- Use unchecked exceptions for domain errors.
- Use checked exceptions only for external systems.
- Wrap infrastructure exceptions with meaningful context.

## **7.2 Never Swallow Exceptions**
- Always log or rethrow with context.

---

# **8. Logging & Observability**

## **8.1 Logging**
- Use structured logging.
- Never log sensitive data.
- INFO for lifecycle, DEBUG for detail, ERROR for failures.

## **8.2 Metrics**
- Expose counters/timers for critical flows.

## **8.3 Tracing**
- Propagate correlation IDs through layers.

---

# **9. Testing (Spock BDD Style)**

## **9.1 Test Structure**
Use `given / when / then`.

## **9.2 Test Types**
- Unit tests for domain logic
- Integration tests for infrastructure
- Contract tests for external boundaries

## **9.3 Test Naming**
```
def "should calculate total damage when critical hit occurs"()
```

## **9.4 Mocking Rules**
- Mock only external dependencies.
- Never mock domain objects.

---

# **10. CI/CD & Quality**

## **10.1 Compilation**
- Code must compile without warnings.
- Enable `-Werror` where possible.

## **10.2 Static Analysis**
- Use Checkstyle, SpotBugs, Sonar rules aligned with these standards.

## **10.3 Formatting**
- Use a consistent formatter (e.g., Google Java Format).

## **10.4 Commit Discipline**
- Small, frequent commits.
- Refactor continuously.

---

# **11. Refactoring & Maintainability**

## **11.1 Continuous Refactoring**
- Refactor whenever duplication or confusion appears.
- Keep refactors small and safe.

## **11.2 Fowler’s Catalog**
Use standard refactorings:

- Extract Method
- Extract Class
- Replace Conditional with Polymorphism
- Introduce Parameter Object
- Replace Magic Number with Constant

## **11.3 Avoid Premature Abstraction**
- Duplication is cheaper than the wrong abstraction.

---

# **12. Documentation**

## **12.1 Javadoc**
- Use only when behaviour is non‑obvious.
- Prefer self‑explanatory code.

## **12.2 ADRs**
- Document architectural decisions in lightweight ADRs.

---

# **13. Build & Dependencies**

## **13.1 Keep Dependencies Minimal**
- Avoid frameworks that obscure domain logic.

## **13.2 Spring Boot**
- Use auto‑config where appropriate.
- Keep domain pure and framework‑agnostic.

## **13.3 Version Pinning**
- Always pin versions in Gradle.