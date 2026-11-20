package com.saucedemo.pages;

import com.microsoft.playwright.Page;

/** The cart page, {@code /cart.html}. */
public final class CartPage extends AppPage {

  public CartPage(Page page) {
    super(page);
  }

  @Override
  protected String path() {
    return "/cart.html";
  }

  @Override
  protected String expectedTitle() {
    return "Your Cart";
  }

  public CheckoutInformationPage checkout() {
    Navigation.click(page, button("Checkout"));
    return new CheckoutInformationPage(page);
  }

  public InventoryPage continueShopping() {
    Navigation.click(page, button("Continue Shopping"));
    return new InventoryPage(page);
  }
}
