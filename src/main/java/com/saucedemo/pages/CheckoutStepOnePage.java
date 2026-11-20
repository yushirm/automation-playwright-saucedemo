package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Checkout step one, {@code /checkout-step-one.html}: the customer information form. */
public class CheckoutStepOnePage {

  private final Page page;

  /** Wraps the given page. */
  public CheckoutStepOnePage(Page page) {
    this.page = page;
  }

  public Locator getTitle() {
    return page.locator("[data-test='title']");
  }

  /** Fills in the customer details and clicks Continue. */
  public void fillForm(String firstName, String lastName, String postalCode) {
    page.getByPlaceholder("First Name").fill(firstName);
    page.getByPlaceholder("Last Name").fill(lastName);
    page.getByPlaceholder("Zip/Postal Code").fill(postalCode);
    page.locator("[data-test='continue']").click();
  }

  /** Clicks Cancel to go back to the cart. */
  public void cancel() {
    page.locator("[data-test='cancel']").click();
  }
}
