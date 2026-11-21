package com.saucedemo.tests;

import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Checkout Complete Tests")
class CheckoutCompleteTests {

  private Page page;
  private CheckoutCompletePage complete;

  @BeforeEach
  void placeOrder(SauceDemo app, Page page) {
    this.page = page;
    complete = app.shopper(STANDARD).adds(BACKPACK).checksOutAs(validCustomer()).confirms();
  }

  @Test
  @DisplayName("Clicking back to home returns to inventory")
  void clickBackToHome_returnsToInventory() {
    assertOpen(page, complete.backHome());
  }
}
