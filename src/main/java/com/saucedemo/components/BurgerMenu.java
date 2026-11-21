package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.Navigation;

/**
 * The side menu behind the button at the top left. While it is closed the site hides it from
 * assistive technology, so a role locator finds nothing until it is open.
 */
public final class BurgerMenu {

  /** The entries, with the role each is exposed as. About is a real link; the rest are buttons. */
  public enum Item {
    ALL_ITEMS("All Items", AriaRole.BUTTON),
    DYNAMIC_CATALOG("Dynamic Catalog", AriaRole.BUTTON),
    ABOUT("About", AriaRole.LINK),
    LOGOUT("Logout", AriaRole.BUTTON),
    RESET_APP_STATE("Reset App State", AriaRole.BUTTON);

    private final String label;
    private final AriaRole role;

    Item(String label, AriaRole role) {
      this.label = label;
      this.role = role;
    }

    public String label() {
      return label;
    }
  }

  private final Page page;

  public BurgerMenu(Page page) {
    this.page = page;
  }

  public Locator openButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Menu"));
  }

  public BurgerMenu open() {
    openButton().click();
    return this;
  }

  public void close() {
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close Menu")).click();
  }

  public Locator item(Item item) {
    return page.getByRole(
        item.role, new Page.GetByRoleOptions().setName(item.label).setExact(true));
  }

  /** Goes to the inventory. Use it from another page: from the inventory the URL doesn't change. */
  public InventoryPage allItems() {
    Navigation.click(page, item(Item.ALL_ITEMS));
    return new InventoryPage(page);
  }

  public LoginPage logout() {
    Navigation.click(page, item(Item.LOGOUT));
    return new LoginPage(page);
  }

  public void resetAppState() {
    item(Item.RESET_APP_STATE).click();
  }

  public Locator aboutLink() {
    return item(Item.ABOUT);
  }
}
