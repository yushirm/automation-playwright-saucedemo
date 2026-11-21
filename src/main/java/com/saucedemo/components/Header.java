package com.saucedemo.components;

import com.microsoft.playwright.Page;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.Navigation;

/** The bar along the top of every page behind the login: the menu button and the cart. */
public final class Header {

  private final Page page;

  public Header(Page page) {
    this.page = page;
  }

  public CartBadge cartBadge() {
    return new CartBadge(page);
  }

  /** Opens the cart. Found by test id: its accessible name includes the item count. */
  public CartPage openCart() {
    Navigation.click(page, page.getByTestId("shopping-cart-link"));
    return new CartPage(page);
  }
}
