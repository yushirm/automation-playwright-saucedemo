package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.domain.Customer;
import java.util.Optional;

/** Checkout step one, {@code /checkout-step-one.html}: the customer information form. */
public final class CheckoutInformationPage extends AppPage {

  public CheckoutInformationPage(Page page) {
    super(page);
  }

  @Override
  public String path() {
    return "/checkout-step-one.html";
  }

  @Override
  public Optional<String> expectedTitle() {
    return Optional.of("Checkout: Your Information");
  }

  /** Fills in all three fields without submitting. */
  public CheckoutInformationPage fillIn(Customer customer) {
    textbox("First Name").fill(customer.firstName());
    textbox("Last Name").fill(customer.lastName());
    textbox("Zip/Postal Code").fill(customer.postalCode());
    return this;
  }

  public Locator firstNameField() {
    return textbox("First Name");
  }

  public Locator lastNameField() {
    return textbox("Last Name");
  }

  public Locator postalCodeField() {
    return textbox("Zip/Postal Code");
  }

  /** The validation message, shown above the form when Continue is refused. */
  public Locator error() {
    return page.getByTestId("error");
  }

  /**
   * Clicks Continue once and stays here, to check what the form says. A plain click: when the form
   * is refused nothing navigates, so {@link Navigation#click} would report a dropped click.
   */
  public CheckoutInformationPage submit() {
    button("Continue").click();
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
