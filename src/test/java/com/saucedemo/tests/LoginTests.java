package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.LoginError.USERNAME_AND_PASSWORD_DO_NOT_MATCH;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.User;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.support.SauceDemoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SauceDemoTest
@DisplayName("Login Page Tests")
class LoginTests {

  private static final String UNKNOWN_USERNAME = "invalid_user";

  private Page page;
  private LoginPage login;

  @BeforeEach
  void openLoginPage(SauceDemo app, Page page) {
    this.page = page;
    login = app.loginPage().open();
  }

  @Test
  @DisplayName("Login with valid credentials redirects to inventory page")
  void loginWithValidCredentials_redirectsToInventory() {
    assertOpen(page, login.loginAs(STANDARD));
  }

  @Test
  @DisplayName("Login with invalid credentials displays error message")
  void loginWithInvalidCredentials_displaysErrorMessage() {
    login.attemptLogin(UNKNOWN_USERNAME, User.PASSWORD);

    assertThat(login.error()).containsText(USERNAME_AND_PASSWORD_DO_NOT_MATCH.message());
    assertOpen(page, login);
  }
}
