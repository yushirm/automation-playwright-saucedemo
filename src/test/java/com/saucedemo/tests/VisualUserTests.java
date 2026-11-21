package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.VISUAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saucedemo.SauceDemo;
import com.saucedemo.domain.Product;
import com.saucedemo.domain.SortOrder;
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

  @Test
  @Scenario("USER-V1")
  @KnownIssue("visual_user's inventory price differs from the price in the cart and at checkout")
  @DisplayName("A product costs the same in the inventory as in the cart")
  void inventoryPrice_matchesCartPrice(SauceDemo app) {
    InventoryPage inventory = app.shopper(VISUAL).inventory();
    String inventoryPrice = inventory.product(BACKPACK).price().innerText();
    inventory.product(BACKPACK).addToCart();

    var cart = inventory.header().openCart();

    assertThat(cart.line(BACKPACK).price()).hasText(inventoryPrice);
  }
}
