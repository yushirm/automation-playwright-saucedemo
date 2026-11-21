package com.saucedemo.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** The known-issue tests that failed as expected during this run. */
public final class KnownIssues {

  private static final List<Failure> FAILURES = new CopyOnWriteArrayList<>();

  /** One test that failed as expected. */
  public record Failure(String test, String reason) {}

  private KnownIssues() {}

  static void record(String test, String reason) {
    FAILURES.add(new Failure(test, reason));
  }

  public static List<Failure> failures() {
    return List.copyOf(FAILURES);
  }
}
