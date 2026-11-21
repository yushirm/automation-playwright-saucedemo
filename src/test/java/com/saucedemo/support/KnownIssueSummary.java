package com.saucedemo.support;

import com.saucedemo.support.KnownIssues.Failure;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Locale;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

/**
 * Lists, at the end of the run, the known-issue tests that failed as expected and why. Always
 * printed, zero included, so a run with known defects can't look like one with none. On GitHub
 * Actions it is also written to the job summary. Registered through {@code META-INF/services}.
 */
public final class KnownIssueSummary implements TestExecutionListener {

  private static final System.Logger LOG = System.getLogger(KnownIssueSummary.class.getName());

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    List<Failure> failures = KnownIssues.failures();
    String browser = System.getProperty("browser", "chromium").toLowerCase(Locale.ROOT);
    StringBuilder report = new StringBuilder(80);
    report.append("Known issues failing as expected (").append(browser).append("): ");
    report.append(failures.size());
    for (Failure failure : failures) {
      report.append(System.lineSeparator()).append("  ").append(failure.test());
      report.append(": ").append(failure.reason());
    }
    LOG.log(Level.INFO, report.toString());

    StringBuilder markdown = new StringBuilder(160);
    markdown.append("### Known issues failing as expected (").append(browser).append("): ");
    markdown.append(failures.size()).append("\n\n");
    if (!failures.isEmpty()) {
      markdown.append("| Test | Why it fails |\n|---|---|\n");
      for (Failure failure : failures) {
        markdown.append("| ").append(failure.test()).append(" | ").append(failure.reason());
        markdown.append(" |\n");
      }
    }
    JobSummary.append(markdown.toString());
  }
}
