package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The login page at the site root. */
public class LoginPage {

  private final Page page;

  /** Wraps the given page. */
  public LoginPage(Page page) {
    this.page = page;
  }

  /** Opens the login page. */
  public void navigate() {
    page.navigate("https://www.saucedemo.com");
  }

  /** Fills in the username and password and clicks Login. */
  public void login(String username, String password) {
    page.getByPlaceholder("Username").fill(username);
    page.getByPlaceholder("Password").fill(password);
    page.locator("[data-test='login-button']").click();
  }

  public Locator getErrorMessage() {
    return page.locator("[data-test='error']");
  }
}
