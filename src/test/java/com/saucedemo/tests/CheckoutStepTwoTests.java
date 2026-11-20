package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.CheckoutStepTwoPage;
import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Checkout Step Two Tests")
class CheckoutStepTwoTests extends BaseTest {

  private CheckoutStepTwoPage checkoutStepTwoPage;

  @BeforeEach
  void setUp() {
    loginAsStandardUser();
    InventoryPage inventoryPage = new InventoryPage(page);
    inventoryPage.addBackpackToCart();
    inventoryPage.openCart();
    new CartPage(page).checkout();
    new CheckoutStepOnePage(page).fillForm("Rick", "Grimes", "2293");
    checkoutStepTwoPage = new CheckoutStepTwoPage(page);
  }

  @Test
  @DisplayName("Clicking finish completes the purchase")
  void clickFinish_completesPurchase() {
    checkoutStepTwoPage.finish();

    CheckoutCompletePage completePage = new CheckoutCompletePage(page);
    assertThat(completePage.getTitle()).hasText("Checkout: Complete!");
  }

  @Test
  @DisplayName("Clicking cancel returns to inventory")
  void clickCancel_returnsToInventory() {
    checkoutStepTwoPage.cancel();

    InventoryPage inventoryPage = new InventoryPage(page);
    assertThat(inventoryPage.getTitle()).hasText("Products");
  }
}
