package com.saucedemo;

import com.microsoft.playwright.Page;
import com.saucedemo.domain.User;
import com.saucedemo.flows.Shopper;
import com.saucedemo.pages.LoginPage;

/** The entry point a test is given: the site's address and the first page of every journey. */
public final class SauceDemo {

  /** Where the site lives. Override with {@code -DbaseUrl=...} to point the suite elsewhere. */
  public static final String BASE_URL = System.getProperty("baseUrl", "https://www.saucedemo.com");

  private final Page page;

  public SauceDemo(Page page) {
    this.page = page;
  }

  public LoginPage loginPage() {
    return new LoginPage(page);
  }

  /** Opens the login page, logs in as the given user and returns them on the inventory. */
  public Shopper shopper(User user) {
    return new Shopper(loginPage().open().loginAs(user));
  }
}
