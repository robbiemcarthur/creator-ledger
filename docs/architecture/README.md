# CreatorLedger Architecture Documentation

This directory contains architecture documentation for the CreatorLedger system using the C4 model.

## C4 Model Levels

We use the [C4 model](https://c4model.com/) to document architecture at different levels of abstraction:

1. **System Context** - Shows CreatorLedger in its environment with users and external systems
2. **Container** - Shows the high-level technology choices (applications, databases, etc.)
3. **Component** - Shows the components within each bounded context/module
4. **Code** - Shows implementation details (class diagrams, sequence diagrams)

## Viewing the Diagrams

### In GitHub
All diagrams use **Mermaid** syntax and render automatically in GitHub's Markdown viewer. Just click on any `.md` file.

### In VS Code
Install the **Markdown Preview Mermaid Support** extension:
```bash
code --install-extension bierner.markdown-mermaid
```

### In IntelliJ IDEA
Mermaid diagrams are supported natively in the Markdown preview (2021.1+).

### Online
Copy the Mermaid code and paste into:
- [Mermaid Live Editor](https://mermaid.live/)
- [GitHub Gist](https://gist.github.com/) (renders automatically)

## Diagram Files

- [01-system-context.md](./01-system-context.md) - System Context diagram
- [02-container.md](./02-container.md) - Container diagram
- [03-component-user.md](./03-component-user.md) - User module components
- [03-component-event.md](./03-component-event.md) - Event module components
- [03-component-income.md](./03-component-income.md) - Income module components
- [03-component-expense.md](./03-component-expense.md) - Expense module components
- [03-component-reporting.md](./03-component-reporting.md) - Reporting module components

## Updating Diagrams

**IMPORTANT:** Keep diagrams up to date as the architecture evolves!

When you:
- Add a new module → Update container and component diagrams
- Add a new external system → Update system context diagram
- Change module interactions → Update component diagrams
- Add new infrastructure → Update container diagram

## Architecture Principles

See [ARCHITECTURE.md](../../ARCHITECTURE.md) and [CLAUDE.md](../../CLAUDE.md) for:
- Domain-Driven Design (DDD) principles
- Modulith architecture guidelines
- Event-driven communication patterns
- Testing strategies
