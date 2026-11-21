package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
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
  @Scenario("DONE-01")
  @DisplayName("The order is confirmed and the cart is empty")
  void completeOrder_confirmsAndEmptiesTheCart() {
    assertOpen(page, complete);
    assertThat(complete.heading()).hasText("Thank you for your order!");
    assertThat(complete.header().cartBadge().count()).isHidden();
  }

  @Test
  @Scenario("DONE-02")
  @DisplayName("Clicking back to home returns to inventory")
  void clickBackToHome_returnsToInventory() {
    assertOpen(page, complete.backHome());
  }

  @Test
  @Scenario("DONE-02")
  @DisplayName("Back home leaves the cart empty")
  void clickBackToHome_leavesTheCartEmpty() {
    var inventory = complete.backHome();

    assertThat(inventory.header().cartBadge().count()).isHidden();
    assertThat(inventory.product(BACKPACK).addButton()).isVisible();
  }
}
