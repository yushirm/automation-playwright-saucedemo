package com.saucedemo.domain;

/**
 * The details entered at checkout. Start from {@link #validCustomer()} and change only what a test
 * is about, for example {@code validCustomer().withPostalCode("")}.
 */
public record Customer(String firstName, String lastName, String postalCode) {

  /** A customer whose details pass checkout validation. */
  public static Customer validCustomer() {
    return new Customer("Rick", "Grimes", "2293");
  }

  public Customer withFirstName(String value) {
    return new Customer(value, lastName, postalCode);
  }

  public Customer withLastName(String value) {
    return new Customer(firstName, value, postalCode);
  }

  public Customer withPostalCode(String value) {
    return new Customer(firstName, lastName, value);
  }
}
