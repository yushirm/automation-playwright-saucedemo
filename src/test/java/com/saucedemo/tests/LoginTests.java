package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.LoginError.PASSWORD_REQUIRED;
import static com.saucedemo.domain.LoginError.USERNAME_AND_PASSWORD_DO_NOT_MATCH;
import static com.saucedemo.domain.LoginError.USERNAME_REQUIRED;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.LoginError;
import com.saucedemo.domain.User;
import com.saucedemo.pages.AppPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.time.Instant;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SauceDemoTest
@DisplayName("Login Page Tests")
class LoginTests {

  private static final String UNKNOWN_USERNAME = "invalid_user";
  private static final String WRONG_PASSWORD = "not_the_password";

  private Page page;
  private LoginPage login;

  @BeforeEach
  void openLoginPage(SauceDemo app, Page page) {
    this.page = page;
    login = app.loginPage().open();
  }

  @Test
  @Scenario("LOGIN-01")
  @DisplayName("Login with valid credentials redirects to inventory page")
  void loginWithValidCredentials_redirectsToInventory() {
    assertOpen(page, login.loginAs(STANDARD));
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(value = User.class, mode = EXCLUDE, names = "LOCKED_OUT")
  @Scenario({"LOGIN-01", "LOGIN-03", "LOGIN-04", "LOGIN-05", "LOGIN-06"})
  @DisplayName("Every user who is allowed in lands on the inventory with six products")
  void loginAs_landsOnInventoryWithSixProducts(User user) {
    InventoryPage inventory = login.loginAs(user);

    assertOpen(page, inventory);
    assertThat(inventory.productCards()).hasCount(6);
  }

  @Test
  @Scenario("LOGIN-02")
  @DisplayName("The locked-out user is refused with a message and stays on the login page")
  void loginAsLockedOutUser_isRefused() {
    login.attemptLogin(User.LOCKED_OUT.username(), User.PASSWORD);

    assertThat(login.error()).containsText(LoginError.LOCKED_OUT.message());
    assertOpen(page, login);
    assertThat(login.usernameField()).hasClass(Pattern.compile("\\berror\\b"));
    assertThat(login.passwordField()).hasClass(Pattern.compile("\\berror\\b"));
  }

  @Test
  @Scenario("LOGIN-07")
  @DisplayName("Login with invalid credentials displays error message")
  void loginWithInvalidCredentials_displaysErrorMessage() {
    login.attemptLogin(UNKNOWN_USERNAME, User.PASSWORD);

    assertThat(login.error()).containsText(USERNAME_AND_PASSWORD_DO_NOT_MATCH.message());
    assertOpen(page, login);
  }

  @Test
  @Scenario("LOGIN-08")
  @DisplayName("A known username with the wrong password gets the same message")
  void loginWithWrongPassword_displaysErrorMessage() {
    login.attemptLogin(STANDARD.username(), WRONG_PASSWORD);

    assertThat(login.error()).containsText(USERNAME_AND_PASSWORD_DO_NOT_MATCH.message());
    assertOpen(page, login);
  }

  @Test
  @Scenario("LOGIN-09")
  @DisplayName("Both fields empty asks for a username")
  void loginWithNothingEntered_asksForUsername() {
    login.attemptLogin("", "");

    assertThat(login.error()).containsText(USERNAME_REQUIRED.message());
    assertOpen(page, login);
  }

  @Test
  @Scenario("LOGIN-10")
  @DisplayName("A username with no password asks for a password")
  void loginWithUsernameOnly_asksForPassword() {
    login.attemptLogin(STANDARD.username(), "");

    assertThat(login.error()).containsText(PASSWORD_REQUIRED.message());
    assertOpen(page, login);
  }

  @Test
  @Scenario("LOGIN-11")
  @DisplayName("The error can be dismissed with its close button")
  void dismissError_removesTheMessage() {
    login.attemptLogin("", "");
    assertThat(login.error()).isVisible();

    login.dismissError();

    assertThat(login.error()).hasCount(0);
  }

  @Test
  @Scenario("LOGIN-12")
  @DisplayName("Login sets a session cookie for the user that lasts about ten minutes")
  void loginWithValidCredentials_setsSessionCookie() {
    AppPage inventory = login.loginAs(STANDARD);

    Cookie cookie = inventory.sessionCookie().orElseThrow();
    double secondsLeft = cookie.expires - Instant.now().getEpochSecond();
    assertEquals(STANDARD.username(), cookie.value);
    assertTrue(
        secondsLeft > 8 * 60 && secondsLeft <= 11 * 60,
        "Expected the cookie to last about 10 minutes, but it lasts " + secondsLeft + " s");
  }
}
