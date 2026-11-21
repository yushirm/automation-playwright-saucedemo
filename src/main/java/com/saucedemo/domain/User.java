package com.saucedemo.domain;

/** The accounts SauceDemo provides. Each behaves differently; see docs/SCENARIOS.md. */
public enum User {
  STANDARD("standard_user"),
  LOCKED_OUT("locked_out_user"),
  PROBLEM("problem_user"),
  PERFORMANCE_GLITCH("performance_glitch_user"),
  ERROR("error_user"),
  VISUAL("visual_user");

  /** Every SauceDemo account uses the same password. */
  public static final String PASSWORD = "secret_sauce";

  private final String username;

  User(String username) {
    this.username = username;
  }

  public String username() {
    return username;
  }
}
