package com.saucedemo.support;

import com.saucedemo.pages.Navigation;
import com.saucedemo.pages.Navigation.DroppedClick;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

/**
 * Reports, at the end of the run, how many navigating clicks SauceDemo dropped and the suite
 * retried. Always printed, zero included, so a run with retries can't look like a clean one. On
 * GitHub Actions it is also written to the job summary. Registered through {@code
 * META-INF/services}.
 */
public final class DroppedClickSummary implements TestExecutionListener {

  private static final System.Logger LOG = System.getLogger(DroppedClickSummary.class.getName());

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    List<DroppedClick> dropped = Navigation.droppedClicks();
    String browser = System.getProperty("browser", "chromium").toLowerCase(Locale.ROOT);
    StringBuilder report = new StringBuilder(80);
    report.append("Dropped clicks retried (").append(browser).append("): ").append(dropped.size());
    for (DroppedClick click : dropped) {
      report.append(System.lineSeparator()).append("  ").append(click.control());
      report.append(" on ").append(click.url());
    }
    LOG.log(Level.INFO, report.toString());
    writeJobSummary(browser, dropped);
  }

  private static void writeJobSummary(String browser, List<DroppedClick> dropped) {
    String summaryFile = System.getenv("GITHUB_STEP_SUMMARY");
    if (summaryFile == null || summaryFile.isBlank()) {
      return;
    }
    StringBuilder markdown = new StringBuilder(160);
    markdown
        .append("### Dropped clicks retried (")
        .append(browser)
        .append("): ")
        .append(dropped.size())
        .append("\n\n");
    if (!dropped.isEmpty()) {
      markdown.append("SauceDemo ignored these clicks; the suite clicked once more.\n\n");
      markdown.append("| Page | Control |\n|---|---|\n");
      for (DroppedClick click : dropped) {
        markdown.append("| `").append(click.url()).append("` | `").append(click.control());
        markdown.append("` |\n");
      }
    }
    try {
      Files.writeString(
          Path.of(summaryFile),
          markdown.toString(),
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not write the job summary to " + summaryFile, e);
    }
  }
}
