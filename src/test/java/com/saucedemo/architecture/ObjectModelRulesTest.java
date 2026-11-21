package com.saucedemo.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Page objects, components and flows expose locators and values; tests assert. Fails the build if
 * code under {@code src/main} calls Playwright's assertions.
 */
@DisplayName("Object model rules")
class ObjectModelRulesTest {

  @Test
  @DisplayName("Page objects, components and flows never assert")
  void objectModel_neverAsserts() {
    noClasses()
        .should()
        .dependOnClassesThat()
        .belongToAnyOf(PlaywrightAssertions.class)
        .because("page objects expose locators and values; tests decide what is right")
        .check(
            new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.saucedemo"));
  }
}
