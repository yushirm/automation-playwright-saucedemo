package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Login Page Tests")
class LoginTests extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page);
        loginPage.navigate();
    }

    @Test
    @DisplayName("Login with valid credentials redirects to inventory page")
    void loginWithValidCredentials_redirectsToInventory() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);

        InventoryPage inventoryPage = new InventoryPage(page);
        assertThat(inventoryPage.getTitle()).hasText("Products");
        assertThat(page).hasURL(BASE_URL + "/inventory.html");
    }

    @Test
    @DisplayName("Login with invalid credentials displays error message")
    void loginWithInvalidCredentials_displaysErrorMessage() {
        loginPage.login("invalid_user", VALID_PASSWORD);

        assertThat(loginPage.getErrorMessage()).containsText(
                "Username and password do not match any user in this service");
        assertThat(page).hasURL(BASE_URL + "/");
    }
}
