package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The cart page, {@code /cart.html}. */
public class CartPage {

  private final Page page;

  /** Wraps the given page. */
  public CartPage(Page page) {
    this.page = page;
  }

  public Locator getTitle() {
    return page.locator("[data-test='title']");
  }

  /** Clicks Checkout to go to checkout step one. */
  public void checkout() {
    page.locator("[data-test='checkout']").click();
  }

  /** Clicks Continue Shopping to go back to the inventory. */
  public void continueShopping() {
    page.locator("[data-test='continue-shopping']").click();
  }
}
