package com.saucedemo.tests;

import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;

import com.saucedemo.SauceDemo;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Checkout Step Two Tests")
class CheckoutStepTwoTests {

  private CheckoutOverviewPage overview;

  @BeforeEach
  void reachOverview(SauceDemo app) {
    overview = app.shopper(STANDARD).adds(BACKPACK).checksOutAs(validCustomer());
  }

  @Test
  @DisplayName("Clicking finish completes the purchase")
  void clickFinish_completesPurchase() {
    overview.finish().assertIsOpen();
  }

  @Test
  @DisplayName("Clicking cancel returns to inventory")
  void clickCancel_returnsToInventory() {
    overview.cancel().assertIsOpen();
  }
}
