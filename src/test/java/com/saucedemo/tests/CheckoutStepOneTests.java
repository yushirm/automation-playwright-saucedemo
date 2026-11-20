package com.saucedemo.tests;

import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;

import com.saucedemo.SauceDemo;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Checkout Step One Tests")
class CheckoutStepOneTests {

  private CheckoutInformationPage information;

  @BeforeEach
  void startCheckout(SauceDemo app) {
    information = app.shopper(STANDARD).adds(BACKPACK).opensCart().checkout();
  }

  @Test
  @DisplayName("Submitting valid form data proceeds to checkout step two")
  void submitValidForm_proceedsToStepTwo() {
    information.fillIn(validCustomer()).continueToOverview().assertIsOpen();
  }

  @Test
  @DisplayName("Clicking cancel returns to cart")
  void clickCancel_returnsToCart() {
    information.cancel().assertIsOpen();
  }
}
