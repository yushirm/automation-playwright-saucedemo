package com.saucedemo.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Fails the build if any code in the suite waits for a fixed time. A fixed wait is either too
 * short, and the test is flaky, or too long, and every run pays for it. Wait for a condition
 * instead: Playwright's locators and assertions already retry until it holds.
 */
@DisplayName("No fixed waits")
class NoSleepsTest {

  @Test
  @DisplayName("No code calls Thread.sleep, TimeUnit.sleep or waitForTimeout")
  void noClass_waitsForFixedTime() {
    noClasses()
        .should()
        .callMethod(Thread.class, "sleep", long.class)
        .orShould()
        .callMethod(Thread.class, "sleep", long.class, int.class)
        .orShould()
        .callMethod(Thread.class, "sleep", Duration.class)
        .orShould()
        .callMethod(TimeUnit.class, "sleep", long.class)
        .orShould()
        .callMethod(Page.class, "waitForTimeout", double.class)
        .orShould()
        .callMethod(Frame.class, "waitForTimeout", double.class)
        .because("a fixed wait is either flaky or slow; wait for a condition instead")
        .check(new ClassFileImporter().importPackages("com.saucedemo"));
  }
}
