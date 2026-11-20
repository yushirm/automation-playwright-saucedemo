package com.saucedemo.pages;

import com.microsoft.playwright.Page;

/** The order confirmation page, {@code /checkout-complete.html}. */
public final class CheckoutCompletePage extends AppPage {

  public CheckoutCompletePage(Page page) {
    super(page);
  }

  @Override
  protected String path() {
    return "/checkout-complete.html";
  }

  @Override
  protected String expectedTitle() {
    return "Checkout: Complete!";
  }

  public InventoryPage backHome() {
    Navigation.click(page, button("Back Home"));
    return new InventoryPage(page);
  }
}
