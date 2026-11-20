package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Checkout step two, {@code /checkout-step-two.html}: the order overview. */
public class CheckoutStepTwoPage {

  private final Page page;

  /** Wraps the given page. */
  public CheckoutStepTwoPage(Page page) {
    this.page = page;
  }

  public Locator getTitle() {
    return page.locator("[data-test='title']");
  }

  /** Clicks Finish to place the order. */
  public void finish() {
    page.locator("[data-test='finish']").click();
  }

  /** Clicks Cancel to go back to the inventory. */
  public void cancel() {
    page.locator("[data-test='cancel']").click();
  }
}
