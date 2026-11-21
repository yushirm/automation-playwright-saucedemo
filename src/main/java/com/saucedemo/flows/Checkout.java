package com.saucedemo.flows;

import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutOverviewPage;

/**
 * A checkout in progress, at the overview, in the shopper's terms. Flows speak the domain ({@code
 * confirms()}); the page objects underneath speak the UI ({@code finish()}, the button's label).
 */
public final class Checkout {

  private final CheckoutOverviewPage overview;

  Checkout(CheckoutOverviewPage overview) {
    this.overview = overview;
  }

  public CheckoutOverviewPage overview() {
    return overview;
  }

  /** Places the order. */
  public CheckoutCompletePage confirms() {
    return overview.finish();
  }
}
