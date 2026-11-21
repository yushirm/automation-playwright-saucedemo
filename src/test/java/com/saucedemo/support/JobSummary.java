package com.saucedemo.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Writes to the GitHub Actions job summary, when the run is on GitHub Actions. */
final class JobSummary {

  private JobSummary() {}

  static void append(String markdown) {
    String summaryFile = System.getenv("GITHUB_STEP_SUMMARY");
    if (summaryFile == null || summaryFile.isBlank()) {
      return;
    }
    try {
      Files.writeString(
          Path.of(summaryFile),
          markdown,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not write the job summary to " + summaryFile, e);
    }
  }
}
