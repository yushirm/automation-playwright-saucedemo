package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.CheckoutStepTwoPage;
import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Checkout Step One Tests")
class CheckoutStepOneTests extends BaseTest {

    private CheckoutStepOnePage checkoutStepOnePage;

    @BeforeEach
    void setUp() {
        loginAsStandardUser();
        InventoryPage inventoryPage = new InventoryPage(page);
        inventoryPage.addBackpackToCart();
        inventoryPage.openCart();
        new CartPage(page).checkout();
        checkoutStepOnePage = new CheckoutStepOnePage(page);
    }

    @Test
    @DisplayName("Submitting valid form data proceeds to checkout step two")
    void submitValidForm_proceedsToStepTwo() {
        checkoutStepOnePage.fillForm("Rick", "Grimes", "2293");

        CheckoutStepTwoPage checkoutStepTwoPage = new CheckoutStepTwoPage(page);
        assertThat(checkoutStepTwoPage.getTitle()).hasText("Checkout: Overview");
    }

    @Test
    @DisplayName("Clicking cancel returns to cart")
    void clickCancel_returnsToCart() {
        checkoutStepOnePage.cancel();

        CartPage cartPage = new CartPage(page);
        assertThat(cartPage.getTitle()).hasText("Your Cart");
    }
}
