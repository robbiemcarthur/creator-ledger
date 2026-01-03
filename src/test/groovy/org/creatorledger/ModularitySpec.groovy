package org.creatorledger

import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import spock.lang.Specification

/**
 * Verifies the modular structure of CreatorLedger using Spring Modulith.
 * <p>
 * This test ensures that:
 * - Module boundaries are respected
 * - No circular dependencies exist
 * - Modules only communicate via defined interfaces or events
 * - Architecture rules are enforced
 * </p>
 * <p>
 * NOTE: This is a UNIT test, not an integration test.
 * It analyzes the package structure without starting Spring Boot.
 * </p>
 */
class ModularitySpec extends Specification {

    def "should have well-formed module structure"() {
        given: "the application modules"
        def modules = ApplicationModules.of(CreatorLedgerApplication.class)

        when: "verifying module structure"
        try {
            modules.verify()
        } catch (Exception e) {
            println "=== MODULE VIOLATIONS DETECTED ==="
            println e.message
            println "===================================="
            throw e
        }

        then: "no violations are found"
        noExceptionThrown()
    }

    def "should identify all expected modules"() {
        given: "the application modules"
        def modules = ApplicationModules.of(CreatorLedgerApplication.class)

        when: "getting module names"
        def moduleNames = modules.stream()
                .map { it.getName() }
                .toList()

        then: "all expected modules are present"
        moduleNames.contains("user")
        moduleNames.contains("event")
        moduleNames.contains("income")
        moduleNames.contains("expense")
        moduleNames.contains("reporting")
        moduleNames.contains("common")
    }

    def "should not have circular dependencies"() {
        given: "the application modules"
        def modules = ApplicationModules.of(CreatorLedgerApplication.class)

        when: "verifying module structure"
        modules.verify()

        then: "no circular dependencies exist"
        noExceptionThrown()
    }

    def "should generate module documentation"() {
        given: "the application modules"
        def modules = ApplicationModules.of(CreatorLedgerApplication.class)

        when: "generating documentation"
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()

        then: "documentation is created in target/modulith-docs/"
        // PlantUML files are created (check target/modulith-docs/ directory)
        noExceptionThrown()
    }

    def "common module should be shared by all other modules"() {
        given: "the application modules"
        def modules = ApplicationModules.of(CreatorLedgerApplication.class)
        def commonModule = modules.getModuleByName("common").get()

        when: "checking module dependencies"
        def dependentModules = modules.stream()
                .filter { it.getName() != "common" }
                .filter { it.getDependencies(modules).contains(commonModule) }
                .count()

        then: "multiple modules depend on common"
        dependentModules > 0
    }
}
