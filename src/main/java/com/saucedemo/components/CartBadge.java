package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The item count on the cart icon. It is not shown at all while the cart is empty. */
public final class CartBadge {

  private final Locator count;

  CartBadge(Page page) {
    this.count = page.getByTestId("shopping-cart-badge");
  }

  /** The badge itself, whose text is the number of items. Hidden when the cart is empty. */
  public Locator count() {
    return count;
  }
}
