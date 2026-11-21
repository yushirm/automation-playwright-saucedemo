package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.User;

/** The login page at the site root. */
public final class LoginPage extends BasePage {

  public LoginPage(Page page) {
    super(page);
  }

  @Override
  public String path() {
    return "/";
  }

  public LoginPage open() {
    page.navigate(SauceDemo.BASE_URL + path());
    return this;
  }

  /**
   * Logs in and waits for the inventory. A plain click, not {@link Navigation#click}: the
   * performance glitch user takes about 5 s to log in, and a retry would submit the form twice.
   */
  public InventoryPage loginAs(User user) {
    submit(user.username(), User.PASSWORD);
    InventoryPage inventory = new InventoryPage(page);
    page.waitForURL(SauceDemo.BASE_URL + inventory.path());
    return inventory;
  }

  /** Submits the form with any username and password, and stays here to check the outcome. */
  public LoginPage attemptLogin(String username, String password) {
    submit(username, password);
    return this;
  }

  public Locator usernameField() {
    return textbox("Username");
  }

  public Locator passwordField() {
    return textbox("Password");
  }

  /** Closes the error with its dismiss button. */
  public LoginPage dismissError() {
    button("Dismiss error").click();
    return this;
  }

  public Locator error() {
    return page.getByTestId("error");
  }

  private void submit(String username, String password) {
    textbox("Username").fill(username);
    textbox("Password").fill(password);
    button("Login").click();
  }
}
