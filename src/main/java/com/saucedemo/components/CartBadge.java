package com.saucedemo.components;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The item count on the cart icon. It is not shown at all while the cart is empty. */
public final class CartBadge {

  private final Locator badge;

  CartBadge(Page page) {
    this.badge = page.getByTestId("shopping-cart-badge");
  }

  public void assertShows(int items) {
    assertThat(badge).hasText(Integer.toString(items));
  }

  public void assertHidden() {
    assertThat(badge).isHidden();
  }
}
