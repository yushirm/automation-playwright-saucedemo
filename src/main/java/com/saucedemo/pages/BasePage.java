package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.SauceDemo;

/**
 * What every page has: the browser page it drives and the path it lives at.
 *
 * <p>Page objects speak the UI and never assert. They expose locators and values; tests decide what
 * is right. {@code ObjectModelRulesTest} enforces this.
 */
public abstract class BasePage {

  protected final Page page;

  protected BasePage(Page page) {
    this.page = page;
  }

  /** The page's path below {@link SauceDemo#BASE_URL}, for example {@code /cart.html}. */
  public abstract String path();

  /** Goes straight to this page by its address, as a visitor who typed the URL would. */
  public void openDirectly() {
    page.navigate(url());
  }

  /** A button found by its accessible name, matched exactly. */
  protected Locator button(String name) {
    return page.getByRole(AriaRole.BUTTON, exactName(name));
  }

  /** A text field found by its accessible name, matched exactly. */
  protected Locator textbox(String name) {
    return page.getByRole(AriaRole.TEXTBOX, exactName(name));
  }

  /** The page's full address. Tests assert on it; page objects never assert. */
  public String url() {
    return SauceDemo.BASE_URL + path();
  }

  private static Page.GetByRoleOptions exactName(String name) {
    return new Page.GetByRoleOptions().setName(name).setExact(true);
  }
}
