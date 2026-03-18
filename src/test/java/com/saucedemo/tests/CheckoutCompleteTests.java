package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.CheckoutStepTwoPage;
import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Checkout Complete Tests")
class CheckoutCompleteTests extends BaseTest {

    private CheckoutCompletePage checkoutCompletePage;

    @BeforeEach
    void setUp() {
        loginAsStandardUser();
        InventoryPage inventoryPage = new InventoryPage(page);
        inventoryPage.addBackpackToCart();
        inventoryPage.openCart();
        new CartPage(page).checkout();
        new CheckoutStepOnePage(page).fillForm("Rick", "Grimes", "2293");
        new CheckoutStepTwoPage(page).finish();
        checkoutCompletePage = new CheckoutCompletePage(page);
    }

    @Test
    @DisplayName("Clicking back to home returns to inventory")
    void clickBackToHome_returnsToInventory() {
        checkoutCompletePage.backToHome();

        InventoryPage inventoryPage = new InventoryPage(page);
        assertThat(inventoryPage.getTitle()).hasText("Products");
    }
}
