# PlantUML Viewing Guide

Spring Modulith automatically generates PlantUML diagrams showing your module structure and dependencies. This guide explains how to view and work with these diagrams.

## Generating Diagrams

Diagrams are generated when you run the ModularitySpec test:

```bash
./gradlew test --tests "org.creatorledger.ModularitySpec"
```

This creates `.puml` files in `build/spring-modulith-docs/`:

- `components.puml` - Overview showing all modules and their relationships
- `module-<name>.puml` - Individual diagrams for each module showing its dependencies

## Viewing Options

### 1. IntelliJ IDEA (Recommended)

**Built-in Support:**
- IntelliJ Ultimate has native PlantUML support
- Community Edition requires the "PlantUML Integration" plugin

**Steps:**
1. Install PlantUML plugin (if using Community Edition):
   - Settings → Plugins → Search "PlantUML Integration" → Install
2. Install Graphviz (required for rendering):
   ```bash
   # Windows (via Chocolatey)
   choco install graphviz

   # macOS
   brew install graphviz

   # Ubuntu/Debian
   sudo apt-get install graphviz
   ```
3. Open any `.puml` file in IntelliJ
4. Click the PlantUML tool window or use the split view

**Features:**
- Live preview as you edit
- Export to PNG, SVG, or other formats
- Zoom and pan
- Copy diagram as image

### 2. Visual Studio Code

**Plugin:**
- Install "PlantUML" extension by jebbs

**Requirements:**
- Java runtime (already have this for Gradle)
- Graphviz (see IntelliJ section above)

**Usage:**
- Open `.puml` file
- Press `Alt+D` for preview
- Right-click → "PlantUML: Export Current Diagram" for PNG/SVG

### 3. Online Viewer

**PlantText:**
- Visit: https://www.planttext.com/
- Copy-paste the contents of a `.puml` file
- Click "Refresh" to render

**PlantUML Server:**
- Visit: http://www.plantuml.com/plantuml/uml
- Paste diagram code
- View rendered diagram

**Note:** Online viewers may not support all C4 model features used by Spring Modulith.

### 4. Command Line

**Install PlantUML CLI:**
```bash
# macOS
brew install plantuml

# Ubuntu/Debian
sudo apt-get install plantuml

# Windows (via Chocolatey)
choco install plantuml
```

**Generate PNGs:**
```bash
# All diagrams
plantuml build/spring-modulith-docs/*.puml

# Single diagram
plantuml build/spring-modulith-docs/components.puml
```

This creates PNG files alongside the `.puml` files.

## Understanding the Diagrams

### components.puml

Shows all modules in the system and their dependencies:

- **Boxes** = Modules
- **Arrows** = Dependencies between modules
- The diagram clearly shows:
  - All modules depend on `common` (Shared Kernel)
  - `reporting` depends on `expense` (via the api interface)
  - No circular dependencies

### module-*.puml

Individual module diagrams show:

- The focal module
- Its direct dependencies
- Dependencies of those dependencies (transitive)

Example: `module-reporting.puml` shows:
- Reporting module depends on Expense and Common
- Expense depends on Common (transitive)

## Adding to Version Control

**Current Setup:**
- PlantUML files are in `build/` (gitignored)
- Generated on-demand by running tests
- Not committed to repository

**Why This Works:**
- Diagrams are derived artifacts (generated from code)
- Always up-to-date when regenerated
- No risk of diagrams becoming stale

**Alternative (if you want to commit them):**

1. Change output directory in `ModularitySpec.groovy`:
   ```groovy
   new Documenter(modules)
       .writeModulesAsPlantUml(Documenter.DiagramOptions.defaults()
           .withTargetFileName("docs/architecture/spring-modulith/components"))
   ```

2. Add to git:
   ```bash
   git add docs/architecture/spring-modulith/*.puml
   ```

3. Consider using GitHub Actions to auto-generate PNGs

## Tips

1. **Refresh after code changes**: Run the test again to update diagrams
2. **Export for documentation**: Generate PNGs to include in wikis or READMEs
3. **Compare over time**: Keep old PNGs to see how architecture evolves
4. **CI/CD integration**: Generate diagrams in your build pipeline

## Troubleshooting

**"Cannot find Graphviz"**
- Ensure Graphviz is installed and on your PATH
- Restart your IDE/terminal after installing

**"Diagram not rendering"**
- Check the `.puml` file for syntax errors
- Ensure PlantUML plugin is up-to-date
- Try a different viewer to isolate the issue

**"C4 diagrams not working online"**
- Online viewers may not support C4 includes
- Use local tools (IntelliJ/VSCode) for full support
- Or simplify diagrams by removing C4 includes

## Further Reading

- [PlantUML Official Site](https://plantuml.com/)
- [C4 Model](https://c4model.com/)
- [Spring Modulith Docs - Documentation](https://docs.spring.io/spring-modulith/reference/documentation.html)
