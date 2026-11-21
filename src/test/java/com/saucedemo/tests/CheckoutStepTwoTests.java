package com.saucedemo.tests;

import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Checkout Step Two Tests")
class CheckoutStepTwoTests {

  private Page page;
  private CheckoutOverviewPage overview;

  @BeforeEach
  void reachOverview(SauceDemo app, Page page) {
    this.page = page;
    overview = app.shopper(STANDARD).adds(BACKPACK).checksOutAs(validCustomer()).overview();
  }

  @Test
  @DisplayName("Clicking finish completes the purchase")
  void clickFinish_completesPurchase() {
    assertOpen(page, overview.finish());
  }

  @Test
  @DisplayName("Clicking cancel returns to inventory")
  void clickCancel_returnsToInventory() {
    assertOpen(page, overview.cancel());
  }
}
