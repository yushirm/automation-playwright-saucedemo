package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.Navigation;

/** The bar along the top of every page behind the login: the menu button and the cart. */
public final class Header {

  private final Page page;

  public Header(Page page) {
    this.page = page;
  }

  /** The app's name beside the menu button. It has no test id or role, so it is found by text. */
  public Locator logo() {
    return page.getByText("Swag Labs", new Page.GetByTextOptions().setExact(true));
  }

  public CartBadge cartBadge() {
    return new CartBadge(page);
  }

  /** The cart icon. Found by test id: its accessible name includes the item count. */
  public Locator cartLink() {
    return page.getByTestId("shopping-cart-link");
  }

  /** Opens the cart. Found by test id: its accessible name includes the item count. */
  public CartPage openCart() {
    Navigation.click(page, cartLink());
    return new CartPage(page);
  }
}
