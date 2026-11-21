package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.Product.BIKE_LIGHT;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.components.CartLine;
import com.saucedemo.domain.Product;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.support.KnownIssue;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SauceDemoTest
@DisplayName("Cart Page Tests")
class CartTests {

  private static CartPage cartWithBackpack(SauceDemo app) {
    return app.shopper(STANDARD).adds(BACKPACK).opensCart();
  }

  @Test
  @Scenario("CART-05")
  @DisplayName("Clicking checkout navigates to checkout step one")
  void clickCheckout_navigatesToCheckoutStepOne(SauceDemo app, Page page) {
    assertOpen(page, cartWithBackpack(app).checkout());
  }

  @Test
  @Scenario("CART-04")
  @DisplayName("Clicking continue shopping returns to inventory")
  void clickContinueShopping_returnsToInventory(SauceDemo app, Page page) {
    assertOpen(page, cartWithBackpack(app).continueShopping());
  }

  @Test
  @Scenario("CART-01")
  @DisplayName("The cart page is titled Your Cart")
  void openCart_showsTitle(SauceDemo app, Page page) {
    assertOpen(page, cartWithBackpack(app));
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(Product.class)
  @Scenario("CART-02")
  @DisplayName("The cart lists a product with its quantity, name, description and price")
  void cartLine_showsQuantityNameDescriptionAndPrice(Product product, SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).inventory();
    String description = inventory.product(product).description().innerText();
    inventory.product(product).addToCart();

    CartLine line = inventory.header().openCart().line(product);

    assertThat(line.quantity()).hasText("1");
    assertThat(line.name()).hasText(product.displayName());
    assertThat(line.description()).hasText(description);
    assertThat(line.price()).hasText("$" + product.price());
  }

  @Test
  @Scenario("CART-02")
  @DisplayName("The cart lists each product that was added and nothing else")
  void cart_listsExactlyWhatWasAdded(SauceDemo app) {
    CartPage twoItems = app.shopper(STANDARD).adds(BACKPACK, BIKE_LIGHT).opensCart();

    assertThat(twoItems.lines()).hasCount(2);
    assertThat(twoItems.line(BACKPACK).name()).isVisible();
    assertThat(twoItems.line(BIKE_LIGHT).name()).isVisible();
  }

  @Test
  @Scenario("CART-03")
  @DisplayName("Removing a product from the cart drops its line and the badge")
  void removeFromCart_dropsLineAndBadge(SauceDemo app) {
    CartPage cart = cartWithBackpack(app);
    assertThat(cart.header().cartBadge().count()).hasText("1");

    cart.line(BACKPACK).removeButton().click();

    assertThat(cart.lines()).hasCount(0);
    assertThat(cart.header().cartBadge().count()).isHidden();
  }

  @Test
  @Scenario("CART-06")
  @DisplayName("The cart survives a page reload")
  void reloadCart_keepsTheItems(SauceDemo app) {
    CartPage cart = cartWithBackpack(app);
    cart.reload();

    assertThat(cart.header().cartBadge().count()).hasText("1");
    assertThat(cart.line(BACKPACK).name()).isVisible();
  }

  @Test
  @Scenario("CART-07")
  @DisplayName("The cart survives logging out and back in")
  void logoutAndLogin_keepsTheItems(SauceDemo app) {
    InventoryPage inventory =
        cartWithBackpack(app).continueShopping().menu().open().logout().loginAs(STANDARD);

    assertThat(inventory.header().cartBadge().count()).hasText("1");
    assertThat(inventory.product(BACKPACK).removeButton()).isVisible();
  }

  /**
   * An empty cart should not reach checkout. SauceDemo lets it through to step one, which is
   * reported as a defect (decided in the PR 1 review).
   */
  @Test
  @Scenario("CART-08")
  @KnownIssue("SauceDemo lets an empty cart go through to checkout step one instead of blocking it")
  @DisplayName("Checkout is blocked when the cart is empty")
  void clickCheckoutWithEmptyCart_isBlocked(SauceDemo app, Page page) {
    CartPage empty = app.shopper(STANDARD).opensCart();

    empty.clickCheckout();

    CheckoutInformationPage stepOne = new CheckoutInformationPage(page);
    assertThat(page).not().hasURL(stepOne.url());
  }
}
