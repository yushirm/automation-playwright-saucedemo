package com.saucedemo.domain;

/** The messages the login page shows when it refuses a login. */
public enum LoginError {
  USERNAME_AND_PASSWORD_DO_NOT_MATCH(
      "Epic sadface: Username and password do not match any user in this service"),
  LOCKED_OUT("Epic sadface: Sorry, this user has been locked out."),
  USERNAME_REQUIRED("Epic sadface: Username is required"),
  PASSWORD_REQUIRED("Epic sadface: Password is required");

  private final String message;

  LoginError(String message) {
    this.message = message;
  }

  /** The message for a visitor who opened a protected page without logging in. */
  public static String notLoggedIn(String path) {
    return "Epic sadface: You can only access '" + path + "' when you are logged in.";
  }

  public String message() {
    return message;
  }
}
