package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The product list, {@code /inventory.html}. */
public class InventoryPage {

  private final Page page;

  /** Wraps the given page. */
  public InventoryPage(Page page) {
    this.page = page;
  }

  public Locator getTitle() {
    return page.locator("[data-test='title']");
  }

  /** Adds the Sauce Labs Backpack to the cart. */
  public void addBackpackToCart() {
    page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
  }

  /** Removes the Sauce Labs Backpack from the cart. */
  public void removeBackpackFromCart() {
    page.locator("[data-test='remove-sauce-labs-backpack']").click();
  }

  public Locator getCartBadge() {
    return page.locator("[data-test='shopping-cart-badge']");
  }

  /** Opens the cart from the header cart icon. */
  public void openCart() {
    page.locator("[data-test='shopping-cart-link']").click();
  }
}
