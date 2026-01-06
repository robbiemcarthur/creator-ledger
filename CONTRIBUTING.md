# Contributing to CreatorLedger

Thank you for contributing!  
This project follows strict engineering discipline to ensure clarity, maintainability, and learning value.

---

# 1. Development Philosophy

CreatorLedger uses:

- **TDD** (test first, always)
- **DDD** (domain‑driven design)
- **Spring Modulith** (modular monolith)
- **CUPID** (composable, predictable, idiomatic)
- **Tidy First** (structural changes separate from behavioural)

---

# 2. Branching & Commit Rules

### Branching
- `main` — stable, deployable
- `feature/*` — new features
- `refactor/*` — structural improvements
- `fix/*` — bug fixes

### Commit Messages
Use:

- `feat:` for behavioural changes
- `refactor:` for structural changes
- `test:` for test‑only changes
- `fix:` for bug fixes

### Commit Requirements
A commit is allowed only when:

- **`./gradlew check` passes** (this runs tests, SpotBugs, JaCoCo coverage, all quality checks)
- All tests pass
- No warnings
- No SpotBugs violations
- Code coverage meets minimum threshold
- Single logical change
- Behavioural and structural changes are separate

**CRITICAL**: ALWAYS run `./gradlew check` before committing. Never commit without running the full check suite.

---

# 3. TDD Workflow

1. Write a **failing Spock test**
2. Implement **minimum code** to pass
3. Refactor (only when green)
4. Commit
5. Repeat

---

# 4. Module Rules

- Never import domain classes across modules
- Communicate via domain events only
- Domain layer contains no Spring/JPA annotations
- Infrastructure layer contains persistence logic

---

# 5. Code Style

- Prefer value objects over primitives
- Use records for immutable VOs
- Use static factories
- Avoid setters
- Keep classes small and focused
- Avoid anemic domain models

---

# 6. Testing Style

- Use Spock with BDD narratives
- Behaviour‑focused test names
- Unit tests preferred over integration tests
- Use Testcontainers for DB/Kafka tests

---

# 7. Pull Requests

A PR must include:

- Description of behaviour added or structure changed
- Tests demonstrating behaviour
- Explanation of design decisions
- Confirmation that all tests pass

---

# 8. Questions

If unsure about domain modelling, event design, or architecture, ask before coding.