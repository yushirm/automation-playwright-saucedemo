package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.Navigation;

/**
 * The side menu behind the button at the top left. All Items, Logout and Reset App State are
 * anchors without a real link, so they have no link role and are found by test id. About is a real
 * link.
 */
public final class BurgerMenu {

  private final Page page;

  public BurgerMenu(Page page) {
    this.page = page;
  }

  public BurgerMenu open() {
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Menu")).click();
    return this;
  }

  public void close() {
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close Menu")).click();
  }

  public InventoryPage allItems() {
    page.getByTestId("inventory-sidebar-link").click();
    return new InventoryPage(page);
  }

  public LoginPage logout() {
    Navigation.click(page, page.getByTestId("logout-sidebar-link"));
    return new LoginPage(page);
  }

  public void resetAppState() {
    page.getByTestId("reset-sidebar-link").click();
  }

  public Locator aboutLink() {
    return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("About"));
  }
}
