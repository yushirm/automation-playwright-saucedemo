package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.ERROR;
import static com.saucedemo.support.PageAssertions.assertOpen;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.CheckoutError;
import com.saucedemo.domain.Product;
import com.saucedemo.domain.SortOrder;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.support.KnownIssue;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * {@code error_user} is one of SauceDemo's deliberately broken accounts. Each test asserts what a
 * working site would do, so each is a known issue: it is expected to fail, and says why.
 */
@SauceDemoTest
@DisplayName("error_user: deliberate defects")
class ErrorUserTests {

  /** The default order is already name A to Z, so it can't show that sorting is ignored. */
  @ParameterizedTest(name = "{0}")
  @EnumSource(value = SortOrder.class, mode = EXCLUDE, names = "NAME_A_TO_Z")
  @Scenario("USER-E1")
  @KnownIssue("error_user's sort control is ignored and raises a JavaScript error")
  @DisplayName("Sorting puts the products in the chosen order")
  void sortBy_ordersProducts(SortOrder order, SauceDemo app) {
    InventoryPage inventory = app.shopper(ERROR).inventory().sortBy(order);

    assertThat(inventory.productNameLabels()).hasText(order.expectedNames().toArray(String[]::new));
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
      value = Product.class,
      names = {"BOLT_T_SHIRT", "FLEECE_JACKET", "RED_T_SHIRT"})
  @Scenario("USER-E2")
  @KnownIssue("error_user cannot add the Bolt T-Shirt, the Fleece Jacket or the Red T-Shirt")
  @DisplayName("Adding a product shows one on the cart badge")
  void addToCart_incrementsCartBadge(Product product, SauceDemo app) {
    InventoryPage inventory = app.shopper(ERROR).inventory();

    inventory.product(product).addToCart();

    assertThat(inventory.header().cartBadge().count()).hasText("1");
  }

  @Test
  @Scenario("USER-E3")
  @KnownIssue("error_user's Remove fails with \"Failed to remove item from cart.\"")
  @DisplayName("Removing a product hides the cart badge")
  void removeItemFromCart_hidesCartBadge(SauceDemo app) {
    InventoryPage inventory = app.shopper(ERROR).adds(BACKPACK).inventory();
    assertThat(inventory.header().cartBadge().count()).hasText("1");

    inventory.product(BACKPACK).remove();

    assertThat(inventory.header().cartBadge().count()).isHidden();
  }

  @Test
  @Scenario("USER-E4")
  @KnownIssue("error_user's last name cannot be typed")
  @DisplayName("The last name typed at checkout stays in its field")
  void fillInDetails_keepsTheLastName(SauceDemo app) {
    CheckoutInformationPage information =
        app.shopper(ERROR).adds(BACKPACK).opensCart().checkout().fillIn(validCustomer());

    assertThat(information.lastNameField()).hasValue(validCustomer().lastName());
  }

  @Test
  @Scenario("USER-E4")
  @KnownIssue("error_user's checkout skips validation: Continue proceeds without a last name")
  @DisplayName("Continuing without a last name is refused")
  void submitWithoutLastName_isRefused(SauceDemo app, Page page) {
    CheckoutInformationPage information =
        app.shopper(ERROR)
            .adds(BACKPACK)
            .opensCart()
            .checkout()
            .fillIn(validCustomer().withLastName(""));

    information.submit();

    assertThat(information.error()).hasText(CheckoutError.LAST_NAME_REQUIRED.message());
    assertOpen(page, information);
  }

  @Test
  @Scenario("USER-E5")
  @KnownIssue("error_user's Finish does nothing and raises a JavaScript error")
  @DisplayName("Clicking finish completes the purchase")
  void clickFinish_completesPurchase(SauceDemo app, Page page) {
    CheckoutOverviewPage overview =
        app.shopper(ERROR).adds(BACKPACK).checksOutAs(validCustomer()).overview();

    overview.clickFinish();

    assertOpen(page, new CheckoutCompletePage(page));
  }
}
