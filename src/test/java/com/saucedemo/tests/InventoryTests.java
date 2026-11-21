package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.Product;
import com.saucedemo.domain.SortOrder;
import com.saucedemo.domain.User;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SauceDemoTest
@DisplayName("Inventory Page Tests")
class InventoryTests {

  @ParameterizedTest(name = "{0}")
  @EnumSource(value = User.class, mode = EXCLUDE, names = "LOCKED_OUT")
  @Scenario("INV-01")
  @DisplayName("Every user who is allowed in sees the six products, A to Z")
  void inventory_listsSixProductsByName(User user, SauceDemo app) {
    InventoryPage inventory = app.shopper(user).inventory();

    assertThat(inventory.productNameLabels())
        .hasText(SortOrder.NAME_A_TO_Z.expectedNames().toArray(String[]::new));
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(Product.class)
  @Scenario("INV-01")
  @DisplayName("A product shows its name, description, price, picture and an Add to cart button")
  void productCard_showsNameDescriptionPricePictureAndButton(Product product, SauceDemo app) {
    var card = app.shopper(STANDARD).inventory().product(product);

    assertThat(card.name()).hasText(product.displayName());
    assertThat(card.description()).not().isEmpty();
    assertThat(card.price()).hasText("$" + product.price());
    assertThat(card.image()).isVisible();
    assertThat(card.image()).hasAttribute("alt", product.displayName());
    assertThat(card.addButton()).isVisible();
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(SortOrder.class)
  @Scenario({"INV-02", "INV-03", "INV-04", "INV-05"})
  @DisplayName("Sorting puts the products in the chosen order")
  void sortBy_ordersProducts(SortOrder order, SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).inventory().sortBy(order);

    assertThat(inventory.productNameLabels()).hasText(order.expectedNames().toArray(String[]::new));
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(Product.class)
  @Scenario("INV-06")
  @DisplayName("Adding a product shows one on the cart badge")
  void addItemToCart_incrementsCartBadge(Product product, SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).inventory();

    inventory.product(product).addToCart();

    assertThat(inventory.header().cartBadge().count()).hasText("1");
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(value = User.class, mode = EXCLUDE, names = "LOCKED_OUT")
  @Scenario("INV-06")
  @DisplayName("Every user who is allowed in can add the backpack")
  void addBackpackToCart_incrementsCartBadge(User user, SauceDemo app) {
    InventoryPage inventory = app.shopper(user).inventory();

    inventory.product(BACKPACK).addToCart();

    assertThat(inventory.header().cartBadge().count()).hasText("1");
  }

  @Test
  @Scenario("INV-07")
  @DisplayName("Adding several products counts each one")
  void addSeveralItemsToCart_countsEachOne(SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).inventory();

    Product[] all = Product.values();
    for (int added = 0; added < all.length; added++) {
      inventory.product(all[added]).addToCart();
      assertThat(inventory.header().cartBadge().count()).hasText(String.valueOf(added + 1));
    }
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(Product.class)
  @Scenario("INV-08")
  @DisplayName("Removing a product hides the cart badge")
  void removeItemFromCart_hidesCartBadge(Product product, SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).adds(product).inventory();
    assertThat(inventory.header().cartBadge().count()).hasText("1");

    inventory.product(product).remove();

    assertThat(inventory.header().cartBadge().count()).isHidden();
  }

  @Test
  @Scenario("INV-08")
  @DisplayName("Removing one of two products leaves the badge at one")
  void removeOneOfTwoItems_decrementsCartBadge(SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).adds(BACKPACK, Product.ONESIE).inventory();
    assertThat(inventory.header().cartBadge().count()).hasText("2");

    inventory.product(BACKPACK).remove();

    assertThat(inventory.header().cartBadge().count()).hasText("1");
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(Product.class)
  @Scenario("INV-09")
  @DisplayName("The button changes between Add to cart and Remove")
  void productButton_togglesBetweenAddAndRemove(Product product, SauceDemo app) {
    var card = app.shopper(STANDARD).inventory().product(product);
    assertThat(card.addButton()).isVisible();
    assertThat(card.removeButton()).hasCount(0);

    card.addToCart();
    assertThat(card.removeButton()).isVisible();
    assertThat(card.addButton()).hasCount(0);

    card.remove();
    assertThat(card.addButton()).isVisible();
    assertThat(card.removeButton()).hasCount(0);
  }

  @Test
  @Scenario("CART-01")
  @DisplayName("Clicking shopping cart navigates to cart page")
  void clickShoppingCart_navigatesToCartPage(SauceDemo app, Page page) {
    assertOpen(page, app.shopper(STANDARD).inventory().header().openCart());
  }
}
