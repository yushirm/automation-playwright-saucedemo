package com.saucedemo.pages;

import com.microsoft.playwright.Page;

/** Checkout step two, {@code /checkout-step-two.html}: the order overview. */
public final class CheckoutOverviewPage extends AppPage {

  public CheckoutOverviewPage(Page page) {
    super(page);
  }

  @Override
  protected String path() {
    return "/checkout-step-two.html";
  }

  @Override
  protected String expectedTitle() {
    return "Checkout: Overview";
  }

  public CheckoutCompletePage finish() {
    Navigation.click(page, button("Finish"));
    return new CheckoutCompletePage(page);
  }

  /** Goes back to the inventory, not to step one or the cart. */
  public InventoryPage cancel() {
    Navigation.click(page, button("Cancel"));
    return new InventoryPage(page);
  }
}
