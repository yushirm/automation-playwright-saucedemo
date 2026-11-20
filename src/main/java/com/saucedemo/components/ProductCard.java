package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.domain.Product;

/** One product on the inventory page, found by its exact name. */
public final class ProductCard {

  private final Locator card;

  /**
   * Finds the card whose name matches exactly, so "T-Shirt" can't match two products. Exact text,
   * not a regex: Playwright runs patterns as JavaScript regexes, and Java's {@code Pattern.quote}
   * produces {@code \Q...\E}, which JavaScript doesn't support.
   */
  public ProductCard(Page page, Product product) {
    Locator name =
        page.getByTestId("inventory-item-name")
            .and(page.getByText(product.displayName(), new Page.GetByTextOptions().setExact(true)));
    this.card = page.getByTestId("inventory-item").filter(new Locator.FilterOptions().setHas(name));
  }

  public ProductCard addToCart() {
    button("Add to cart").click();
    return this;
  }

  public ProductCard remove() {
    button("Remove").click();
    return this;
  }

  public Locator price() {
    return card.getByTestId("inventory-item-price");
  }

  private Locator button(String name) {
    return card.getByRole(
        AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(name).setExact(true));
  }
}
