package com.saucedemo.tests;

import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.pages.CartPage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Cart Page Tests")
class CartTests {

  private Page page;
  private CartPage cart;

  @BeforeEach
  void openCartWithBackpack(SauceDemo app, Page page) {
    this.page = page;
    cart = app.shopper(STANDARD).adds(BACKPACK).opensCart();
  }

  @Test
  @DisplayName("Clicking checkout navigates to checkout step one")
  void clickCheckout_navigatesToCheckoutStepOne() {
    assertOpen(page, cart.checkout());
  }

  @Test
  @DisplayName("Clicking continue shopping returns to inventory")
  void clickContinueShopping_returnsToInventory() {
    assertOpen(page, cart.continueShopping());
  }
}
