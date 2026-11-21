package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.domain.Product;

/** Finds the block for one product on a page that lists products. */
final class Cards {

  private Cards() {}

  /**
   * Finds the item whose name matches exactly, so "T-Shirt" can't match two products. Exact text,
   * not a regex: Playwright runs patterns as JavaScript regexes, and Java's {@code Pattern.quote}
   * produces {@code \Q...\E}, which JavaScript doesn't support.
   */
  static Locator forProduct(Page page, Product product) {
    Locator name =
        page.getByTestId("inventory-item-name")
            .and(page.getByText(product.displayName(), new Page.GetByTextOptions().setExact(true)));
    return page.getByTestId("inventory-item").filter(new Locator.FilterOptions().setHas(name));
  }
}
