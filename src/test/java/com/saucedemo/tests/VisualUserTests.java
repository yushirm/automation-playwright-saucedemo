package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.User.VISUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saucedemo.SauceDemo;
import com.saucedemo.domain.Product;
import com.saucedemo.domain.SortOrder;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.support.KnownIssue;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@code visual_user} is one of SauceDemo's deliberately broken accounts. Its layout differences
 * need a screenshot comparison (PR 6); its wrong prices show up in a functional check, here.
 */
@SauceDemoTest
@DisplayName("visual_user: deliberate defects")
class VisualUserTests {

  @Test
  @Scenario("USER-V1")
  @KnownIssue("visual_user's inventory shows wrong prices, such as $70.16 for the Backpack")
  @DisplayName("The inventory shows the catalogue prices")
  void inventoryPrices_matchTheCatalogue(SauceDemo app) {
    InventoryPage inventory = app.shopper(VISUAL).inventory();

    List<BigDecimal> catalogue =
        SortOrder.NAME_A_TO_Z.expectedOrder().stream().map(Product::price).toList();
    assertEquals(catalogue, inventory.productPrices());
  }

  /**
   * The wrong prices are random on each login, so one product's price could match by chance. All
   * six are compared, and the test passes only if every one matches.
   */
  @Test
  @Scenario("USER-V1")
  @KnownIssue("visual_user's inventory prices differ from the prices in the cart and at checkout")
  @DisplayName("Every product costs the same in the inventory as in the cart")
  void inventoryPrices_matchCartPrices(SauceDemo app) {
    InventoryPage inventory = app.shopper(VISUAL).inventory();
    List<BigDecimal> shown = inventory.productPrices();
    Product[] all = Product.values();
    for (Product product : all) {
      inventory.product(product).addToCart();
    }

    CartPage cart = inventory.header().openCart();

    for (Product product : all) {
      BigDecimal inInventory = shown.get(SortOrder.NAME_A_TO_Z.expectedOrder().indexOf(product));
      assertThat(cart.line(product).price()).hasText("$" + inInventory);
    }
  }
}
