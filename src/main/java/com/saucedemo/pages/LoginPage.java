package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The login page at the site root. */
public class LoginPage {

  private final Page page;

  public LoginPage(Page page) {
    this.page = page;
  }

  public void navigate() {
    page.navigate("https://www.saucedemo.com");
  }

  /**
   * Submits the login form. A valid user lands on the inventory; anything else leaves an error on
   * this page.
   */
  public void login(String username, String password) {
    page.getByPlaceholder("Username").fill(username);
    page.getByPlaceholder("Password").fill(password);
    page.locator("[data-test='login-button']").click();
  }

  public Locator getErrorMessage() {
    return page.locator("[data-test='error']");
  }
}
