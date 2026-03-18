package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Cart Page Tests")
class CartTests extends BaseTest {

    private CartPage cartPage;

    @BeforeEach
    void setUp() {
        loginAsStandardUser();
        InventoryPage inventoryPage = new InventoryPage(page);
        inventoryPage.addBackpackToCart();
        inventoryPage.openCart();
        cartPage = new CartPage(page);
    }

    @Test
    @DisplayName("Clicking checkout navigates to checkout step one")
    void clickCheckout_navigatesToCheckoutStepOne() {
        cartPage.checkout();

        CheckoutStepOnePage checkoutPage = new CheckoutStepOnePage(page);
        assertThat(checkoutPage.getTitle()).hasText("Checkout: Your Information");
    }

    @Test
    @DisplayName("Clicking continue shopping returns to inventory")
    void clickContinueShopping_returnsToInventory() {
        cartPage.continueShopping();

        InventoryPage inventoryPage = new InventoryPage(page);
        assertThat(inventoryPage.getTitle()).hasText("Products");
    }
}
