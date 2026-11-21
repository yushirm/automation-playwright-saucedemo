package com.saucedemo.architecture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saucedemo.support.Scenario;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Keeps docs/SCENARIOS.md and the tests in step. Every {@code @Scenario} ID must be a row in the
 * matrix, and every row must be covered by a test or be in the short list below of rows that are
 * deliberately not covered yet.
 */
@DisplayName("Scenario matrix")
class ScenarioCoverageTest {

  /** Rows with no test in this PR, and where each is dealt with. */
  private static final Set<String> NOT_IN_THIS_SUITE_YET =
      Set.of(
          "MENU-06", // Dynamic Catalog: out of scope for v2
          "USER-G1", // performance_glitch_user timing: PR 5
          "USER-G2", "USER-G3", "USER-V2", // visual_user layout: PR 6
          "X-04", // axe accessibility: PR 6
          "X-01", // browsers: covered by the CI matrix, not by a test
          "X-02", "X-03");

  private static final Pattern ROW_ID =
      Pattern.compile("^\\| ([A-Z]+(?:-[A-Z]?\\d+)) \\|", Pattern.MULTILINE);

  @Test
  @DisplayName("Every test names real matrix rows, and every row has a test or a reason")
  void matrixAndTests_agree() throws IOException {
    Set<String> matrix = new TreeSet<>();
    Matcher rows = ROW_ID.matcher(Files.readString(Path.of("docs", "SCENARIOS.md")));
    while (rows.find()) {
      matrix.add(rows.group(1));
    }

    Set<String> covered = new TreeSet<>();
    for (JavaClass testClass : new ClassFileImporter().importPackages("com.saucedemo.tests")) {
      for (JavaMethod method : testClass.getMethods()) {
        method
            .tryGetAnnotationOfType(Scenario.class)
            .ifPresent(scenario -> covered.addAll(Set.of(scenario.value())));
      }
    }

    Set<String> unknown = new TreeSet<>(covered);
    unknown.removeAll(matrix);
    assertTrue(unknown.isEmpty(), "Tests name scenarios that are not in the matrix: " + unknown);

    Set<String> uncovered = new TreeSet<>(matrix);
    uncovered.removeAll(covered);
    uncovered.removeAll(NOT_IN_THIS_SUITE_YET);
    assertTrue(uncovered.isEmpty(), "Matrix rows with no test and no reason: " + uncovered);

    Set<String> wronglyListed = new TreeSet<>(NOT_IN_THIS_SUITE_YET);
    wronglyListed.retainAll(covered);
    assertEquals(Set.of(), wronglyListed, "Listed as not covered, but a test covers them");
  }
}
