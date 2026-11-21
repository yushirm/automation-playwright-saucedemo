package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.PROBLEM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import com.saucedemo.SauceDemo;
import com.saucedemo.domain.Product;
import com.saucedemo.domain.SortOrder;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.support.KnownIssue;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * {@code problem_user} is one of SauceDemo's deliberately broken accounts. Each test asserts what a
 * working site would do, so each is a known issue: it is expected to fail, and says why.
 */
@SauceDemoTest
@DisplayName("problem_user: deliberate defects")
class ProblemUserTests {

  @Test
  @Scenario("USER-P1")
  @KnownIssue("problem_user sees the same picture on every product")
  @DisplayName("Each product has its own picture")
  void inventory_showsDistinctPictures(SauceDemo app) {
    InventoryPage inventory = app.shopper(PROBLEM).inventory();

    assertEquals(Product.values().length, new HashSet<>(inventory.imageSources()).size());
  }

  /** The default order is already name A to Z, so it can't show that sorting is ignored. */
  @ParameterizedTest(name = "{0}")
  @EnumSource(value = SortOrder.class, mode = EXCLUDE, names = "NAME_A_TO_Z")
  @Scenario("USER-P2")
  @KnownIssue("problem_user's sort control is ignored: the products stay in the default order")
  @DisplayName("Sorting puts the products in the chosen order")
  void sortBy_ordersProducts(SortOrder order, SauceDemo app) {
    InventoryPage inventory = app.shopper(PROBLEM).inventory().sortBy(order);

    assertThat(inventory.productNameLabels()).hasText(order.expectedNames().toArray(String[]::new));
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
      value = Product.class,
      names = {"BOLT_T_SHIRT", "FLEECE_JACKET", "RED_T_SHIRT"})
  @Scenario("USER-P3")
  @KnownIssue("problem_user cannot add the Bolt T-Shirt, the Fleece Jacket or the Red T-Shirt")
  @DisplayName("Adding a product shows one on the cart badge")
  void addToCart_incrementsCartBadge(Product product, SauceDemo app) {
    InventoryPage inventory = app.shopper(PROBLEM).inventory();

    inventory.product(product).addToCart();

    assertThat(inventory.header().cartBadge().count()).hasText("1");
  }

  @Test
  @Scenario("USER-P4")
  @KnownIssue("problem_user's Remove button on the inventory page does nothing")
  @DisplayName("Removing a product hides the cart badge")
  void removeItemFromCart_hidesCartBadge(SauceDemo app) {
    InventoryPage inventory = app.shopper(PROBLEM).adds(BACKPACK).inventory();
    assertThat(inventory.header().cartBadge().count()).hasText("1");

    inventory.product(BACKPACK).remove();

    assertThat(inventory.header().cartBadge().count()).isHidden();
  }

  @Test
  @Scenario("USER-P5")
  @KnownIssue("problem_user's last name is typed into the first name field")
  @DisplayName("Each detail typed at checkout stays in its own field")
  void fillInDetails_keepsEachDetailInItsOwnField(SauceDemo app) {
    CheckoutInformationPage information =
        app.shopper(PROBLEM).adds(BACKPACK).opensCart().checkout().fillIn(validCustomer());

    assertThat(information.firstNameField()).hasValue(validCustomer().firstName());
    assertThat(information.lastNameField()).hasValue(validCustomer().lastName());
  }
}
