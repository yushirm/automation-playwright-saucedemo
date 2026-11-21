package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.components.CartLine;
import com.saucedemo.domain.Product;
import java.util.Optional;

/** The cart page, {@code /cart.html}. */
public final class CartPage extends AppPage {

  public CartPage(Page page) {
    super(page);
  }

  @Override
  public String path() {
    return "/cart.html";
  }

  @Override
  public Optional<String> expectedTitle() {
    return Optional.of("Your Cart");
  }

  public CartLine line(Product product) {
    return new CartLine(page, product);
  }

  /** Every line in the cart, one locator each. */
  public Locator lines() {
    return page.getByTestId("inventory-item");
  }

  /**
   * Clicks Checkout once and stays here, to check whether the site lets this cart through. A plain
   * click: if checkout is refused nothing navigates, so {@link Navigation#click} would report a
   * dropped click.
   */
  public CartPage clickCheckout() {
    button("Checkout").click();
    return this;
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
