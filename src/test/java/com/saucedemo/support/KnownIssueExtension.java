package com.saucedemo.support;

import io.qameta.allure.Allure;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

/**
 * Runs a {@link KnownIssue} test and inverts the result.
 *
 * <ul>
 *   <li>It fails on an assertion: the failure is expected. The test is reported as aborted, with
 *       the reason as the message, tagged {@code known-issue} in Allure, and counted in the
 *       summary.
 *   <li>It passes: the site has changed. The test fails and says to remove the annotation.
 *   <li>It fails any other way, such as a locator that matches nothing or a timeout in setup: that
 *       is a broken test, not a known issue, so it fails as usual.
 * </ul>
 */
public final class KnownIssueExtension
    implements TestExecutionExceptionHandler, AfterTestExecutionCallback {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(KnownIssueExtension.class);
  private static final String FAILED_AS_EXPECTED = "failedAsExpected";

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable thrown)
      throws Throwable {
    Optional<String> reason = reason(context);
    if (reason.isEmpty() || !(thrown instanceof AssertionError)) {
      throw thrown;
    }
    context.getStore(NAMESPACE).put(FAILED_AS_EXPECTED, true);
    KnownIssues.record(context.getDisplayName(), reason.get());
    Allure.label("tag", "known-issue");
    Allure.description("Known issue: " + reason.get());
    throw new TestAbortedException("Known issue: " + reason.get(), thrown);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    Optional<String> reason = reason(context);
    boolean failedAsExpected =
        context.getStore(NAMESPACE).getOrDefault(FAILED_AS_EXPECTED, Boolean.class, false);
    if (reason.isPresent() && context.getExecutionException().isEmpty() && !failedAsExpected) {
      throw new AssertionFailedError(
          "Known issue appears fixed: "
              + reason.get()
              + " Remove @KnownIssue so this runs as an ordinary test.");
    }
  }

  private static Optional<String> reason(ExtensionContext context) {
    return context
        .getTestMethod()
        .map(method -> method.getAnnotation(KnownIssue.class))
        .map(KnownIssue::value);
  }
}
