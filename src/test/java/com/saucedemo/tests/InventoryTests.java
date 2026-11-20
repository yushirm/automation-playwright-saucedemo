package com.saucedemo.tests;

import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;

import com.saucedemo.SauceDemo;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Inventory Page Tests")
class InventoryTests {

  private InventoryPage inventory;

  @BeforeEach
  void logIn(SauceDemo app) {
    inventory = app.shopper(STANDARD).inventory();
  }

  @Test
  @DisplayName("Adding item to cart increments cart badge to one")
  void addItemToCart_incrementsCartBadge() {
    inventory.product(BACKPACK).addToCart();

    inventory.header().cartBadge().assertShows(1);
  }

  @Test
  @DisplayName("Removing item from cart hides cart badge")
  void removeItemFromCart_hidesCartBadge() {
    inventory.product(BACKPACK).addToCart();
    inventory.header().cartBadge().assertShows(1);

    inventory.product(BACKPACK).remove();

    inventory.header().cartBadge().assertHidden();
  }

  @Test
  @DisplayName("Clicking shopping cart navigates to cart page")
  void clickShoppingCart_navigatesToCartPage() {
    inventory.header().openCart().assertIsOpen();
  }
}
