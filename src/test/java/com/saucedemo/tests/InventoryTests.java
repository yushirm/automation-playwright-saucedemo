package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Inventory Page Tests")
class InventoryTests {

  private Page page;
  private InventoryPage inventory;

  @BeforeEach
  void logIn(SauceDemo app, Page page) {
    this.page = page;
    inventory = app.shopper(STANDARD).inventory();
  }

  @Test
  @DisplayName("Adding item to cart increments cart badge to one")
  void addItemToCart_incrementsCartBadge() {
    inventory.product(BACKPACK).addToCart();

    assertThat(inventory.header().cartBadge().count()).hasText("1");
  }

  @Test
  @DisplayName("Removing item from cart hides cart badge")
  void removeItemFromCart_hidesCartBadge() {
    inventory.product(BACKPACK).addToCart();
    assertThat(inventory.header().cartBadge().count()).hasText("1");

    inventory.product(BACKPACK).remove();

    assertThat(inventory.header().cartBadge().count()).isHidden();
  }

  @Test
  @DisplayName("Clicking shopping cart navigates to cart page")
  void clickShoppingCart_navigatesToCartPage() {
    assertOpen(page, inventory.header().openCart());
  }
}
