package com.saucedemo.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.SauceDemo;

/** What every page has: the browser page it drives and the path it lives at. */
public abstract class BasePage {

  protected final Page page;

  protected BasePage(Page page) {
    this.page = page;
  }

  /** The page's path below {@link SauceDemo#BASE_URL}, for example {@code /cart.html}. */
  protected abstract String path();

  /** A button found by its accessible name, matched exactly. */
  protected Locator button(String name) {
    return page.getByRole(AriaRole.BUTTON, exactName(name));
  }

  /** A text field found by its accessible name, matched exactly. */
  protected Locator textbox(String name) {
    return page.getByRole(AriaRole.TEXTBOX, exactName(name));
  }

  /** Asserts, with Playwright's retrying assertions, that the browser is on this page. */
  public void assertIsOpen() {
    assertThat(page).hasURL(SauceDemo.BASE_URL + path());
  }

  private static Page.GetByRoleOptions exactName(String name) {
    return new Page.GetByRoleOptions().setName(name).setExact(true);
  }
}
