package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.CheckoutError;
import com.saucedemo.domain.Customer;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SauceDemoTest
@DisplayName("Checkout Step One Tests")
class CheckoutStepOneTests {

  private Page page;
  private CheckoutInformationPage information;

  /** Each way to leave a field empty, in the order the form checks them, with the message. */
  static Stream<Arguments> incompleteCustomers() {
    return Stream.of(
        arguments(
            named("nothing entered", new Customer("", "", "")), CheckoutError.FIRST_NAME_REQUIRED),
        arguments(
            named("first name only", validCustomer().withLastName("").withPostalCode("")),
            CheckoutError.LAST_NAME_REQUIRED),
        arguments(
            named("first and last name", validCustomer().withPostalCode("")),
            CheckoutError.POSTAL_CODE_REQUIRED));
  }

  @BeforeEach
  void startCheckout(SauceDemo app, Page page) {
    this.page = page;
    information = app.shopper(STANDARD).adds(BACKPACK).opensCart().checkout();
  }

  @Test
  @Scenario("INFO-04")
  @DisplayName("Submitting valid form data proceeds to checkout step two")
  void submitValidForm_proceedsToStepTwo() {
    assertOpen(page, information.fillIn(validCustomer()).continueToOverview());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("incompleteCustomers")
  @Scenario({"INFO-01", "INFO-02", "INFO-03"})
  @DisplayName("Submitting incomplete details shows which one is missing")
  void submitIncompleteForm_showsWhichDetailIsMissing(Customer customer, CheckoutError expected) {
    information.fillIn(customer).submit();

    assertThat(information.error()).hasText(expected.message());
    assertOpen(page, information);
  }

  @Test
  @Scenario("INFO-05")
  @DisplayName("Clicking cancel returns to cart")
  void clickCancel_returnsToCart() {
    assertOpen(page, information.cancel());
  }
}
