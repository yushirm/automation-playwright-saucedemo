package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Inventory Page Tests")
class InventoryTests extends BaseTest {

  private InventoryPage inventoryPage;

  @BeforeEach
  void setUp() {
    loginAsStandardUser();
    inventoryPage = new InventoryPage(page);
  }

  @Test
  @DisplayName("Adding item to cart increments cart badge to one")
  void addItemToCart_incrementsCartBadge() {
    inventoryPage.addBackpackToCart();

    assertThat(inventoryPage.getCartBadge()).hasText("1");
  }

  @Test
  @DisplayName("Removing item from cart hides cart badge")
  void removeItemFromCart_hidesCartBadge() {
    inventoryPage.addBackpackToCart();
    assertThat(inventoryPage.getCartBadge()).hasText("1");

    inventoryPage.removeBackpackFromCart();
    assertThat(inventoryPage.getCartBadge()).not().isVisible();
  }

  @Test
  @DisplayName("Clicking shopping cart navigates to cart page")
  void clickShoppingCart_navigatesToCartPage() {
    inventoryPage.openCart();

    CartPage cartPage = new CartPage(page);
    assertThat(cartPage.getTitle()).hasText("Your Cart");
    assertThat(page).hasURL(BASE_URL + "/cart.html");
  }
}
