package com.saucedemo.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test that asserts the correct behaviour of something SauceDemo gets wrong, so it is
 * expected to fail. The value is the reason, in a sentence a reader of the report can act on.
 *
 * <p>The test still runs every time. If it fails on an assertion, it is reported as aborted with
 * the reason and listed in the end-of-run summary; if it passes, the site has been fixed and the
 * test fails, telling you to remove this annotation. It is never disabled.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KnownIssue {

  /** Why the test is expected to fail: what the site does instead of the correct behaviour. */
  String value();
}
