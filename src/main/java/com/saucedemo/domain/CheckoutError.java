package com.saucedemo.domain;

/** The messages checkout step one shows when it refuses incomplete details. */
public enum CheckoutError {
  FIRST_NAME_REQUIRED("Error: First Name is required"),
  LAST_NAME_REQUIRED("Error: Last Name is required"),
  POSTAL_CODE_REQUIRED("Error: Postal Code is required");

  private final String message;

  CheckoutError(String message) {
    this.message = message;
  }

  public String message() {
    return message;
  }
}
