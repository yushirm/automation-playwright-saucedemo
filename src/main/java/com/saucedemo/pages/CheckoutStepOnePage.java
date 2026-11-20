package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Checkout step one, {@code /checkout-step-one.html}: the customer information form. */
public class CheckoutStepOnePage {

  private final Page page;

  public CheckoutStepOnePage(Page page) {
    this.page = page;
  }

  public Locator getTitle() {
    return page.locator("[data-test='title']");
  }

  /** Fills in all three fields, then submits the form with Continue. */
  public void fillForm(String firstName, String lastName, String postalCode) {
    page.getByPlaceholder("First Name").fill(firstName);
    page.getByPlaceholder("Last Name").fill(lastName);
    page.getByPlaceholder("Zip/Postal Code").fill(postalCode);
    page.locator("[data-test='continue']").click();
  }

  /** Goes back to the cart. On step two, Cancel goes to the inventory instead. */
  public void cancel() {
    page.locator("[data-test='cancel']").click();
  }
}
