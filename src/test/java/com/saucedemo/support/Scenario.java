package com.saucedemo.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Names the rows of docs/SCENARIOS.md that a test covers, for example
 * {@code @Scenario("LOGIN-02")}. {@code ScenarioCoverageTest} checks every ID against the matrix,
 * so the coverage count is read from the code and not kept by hand.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scenario {

  /** The scenario IDs, such as {@code LOGIN-02} or {@code USER-P3}. */
  String[] value();
}
