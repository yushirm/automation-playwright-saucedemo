package com.saucedemo.pages;

import com.microsoft.playwright.Page;
import com.saucedemo.domain.Customer;

/** Checkout step one, {@code /checkout-step-one.html}: the customer information form. */
public final class CheckoutInformationPage extends AppPage {

  public CheckoutInformationPage(Page page) {
    super(page);
  }

  @Override
  protected String path() {
    return "/checkout-step-one.html";
  }

  @Override
  protected String expectedTitle() {
    return "Checkout: Your Information";
  }

  /** Fills in all three fields without submitting. */
  public CheckoutInformationPage fillIn(Customer customer) {
    textbox("First Name").fill(customer.firstName());
    textbox("Last Name").fill(customer.lastName());
    textbox("Zip/Postal Code").fill(customer.postalCode());
    return this;
  }

  public CheckoutOverviewPage continueToOverview() {
    Navigation.click(page, button("Continue"));
    return new CheckoutOverviewPage(page);
  }

  /** Goes back to the cart. On the overview page, Cancel goes to the inventory instead. */
  public CartPage cancel() {
    Navigation.click(page, button("Cancel"));
    return new CartPage(page);
  }
}
