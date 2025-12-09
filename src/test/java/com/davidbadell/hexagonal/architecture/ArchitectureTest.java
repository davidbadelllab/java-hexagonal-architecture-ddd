package com.davidbadell.hexagonal.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architecture Tests using ArchUnit
 * 
 * Validates that the hexagonal architecture rules are followed.
 */
@DisplayName("Architecture Tests")
@AnalyzeClasses(packages = "com.davidbadell.hexagonal", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.davidbadell.hexagonal");
    }

    @Nested
    @DisplayName("Layer Dependency Rules")
    class LayerDependencyRules {

        @ArchTest
        @DisplayName("Domain should not depend on application layer")
        static final ArchRule domainShouldNotDependOnApplication =
                noClasses()
                        .that().resideInAPackage("..domain..")
                        .should().dependOnClassesThat()
                        .resideInAPackage("..application..");

        @ArchTest
        @DisplayName("Domain should not depend on infrastructure layer")
        static final ArchRule domainShouldNotDependOnInfrastructure =
                noClasses()
                        .that().resideInAPackage("..domain..")
                        .should().dependOnClassesThat()
                        .resideInAPackage("..infrastructure..");

        @ArchTest
        @DisplayName("Application should not depend on infrastructure layer")
        static final ArchRule applicationShouldNotDependOnInfrastructure =
                noClasses()
                        .that().resideInAPackage("..application..")
                        .should().dependOnClassesThat()
                        .resideInAPackage("..infrastructure..");
    }

    @Nested
    @DisplayName("Domain Layer Rules")
    class DomainLayerRules {

        @ArchTest
        @DisplayName("Domain should only depend on itself and Java standard library")
        static final ArchRule domainShouldOnlyDependOnDomainAndJava =
                classes()
                        .that().resideInAPackage("..domain..")
                        .should().onlyDependOnClassesThat()
                        .resideInAnyPackage(
                                "..domain..",
                                "java..",
                                "javax.."
                        );
    }

    @Nested
    @DisplayName("Hexagonal Architecture")
    class HexagonalArchitectureRules {

        @Test
        @DisplayName("Should follow layered architecture")
        void shouldFollowLayeredArchitecture() {
            ArchRule rule = layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Domain").definedBy("..domain..")
                    .layer("Application").definedBy("..application..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    .whereLayer("Domain").mayNotAccessAnyLayer()
                    .whereLayer("Application").mayOnlyAccessLayers("Domain")
                    .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Application");

            rule.check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Naming Conventions")
    class NamingConventions {

        @ArchTest
        @DisplayName("Use case interfaces should end with UseCase")
        static final ArchRule useCasesShouldHaveProperNaming =
                classes()
                        .that().resideInAPackage("..application.port.in..")
                        .should().haveSimpleNameEndingWith("UseCase");

        @ArchTest
        @DisplayName("Domain events should end with Event")
        static final ArchRule domainEventsShouldHaveProperNaming =
                classes()
                        .that().resideInAPackage("..domain.event..")
                        .and().areNotInterfaces()
                        .should().haveSimpleNameEndingWith("Event");

        @ArchTest
        @DisplayName("Exceptions should end with Exception")
        static final ArchRule exceptionsShouldHaveProperNaming =
                classes()
                        .that().resideInAPackage("..domain.exception..")
                        .should().haveSimpleNameEndingWith("Exception");
    }

    @Nested
    @DisplayName("Adapter Rules")
    class AdapterRules {

        @ArchTest
        @DisplayName("REST controllers should only be in infrastructure adapter in rest package")
        static final ArchRule controllersShouldBeInRestPackage =
                classes()
                        .that().haveSimpleNameEndingWith("Controller")
                        .should().resideInAPackage("..infrastructure.adapter.in.rest..");

        @ArchTest
        @DisplayName("Repository adapters should be in persistence package")
        static final ArchRule repositoryAdaptersShouldBeInPersistencePackage =
                classes()
                        .that().haveSimpleNameEndingWith("RepositoryAdapter")
                        .should().resideInAPackage("..infrastructure.adapter.out.persistence..");
    }
}
