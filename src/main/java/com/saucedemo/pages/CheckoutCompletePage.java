package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The order confirmation page, {@code /checkout-complete.html}. */
public class CheckoutCompletePage {

  private final Page page;

  public CheckoutCompletePage(Page page) {
    this.page = page;
  }

  public Locator getTitle() {
    return page.locator("[data-test='title']");
  }

  public void backToHome() {
    page.locator("[data-test='back-to-products']").click();
  }
}
