package com.saucedemo.pages;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.components.BurgerMenu;
import com.saucedemo.components.Header;

/** A page behind the login, with the shared header and a page title. */
public abstract class AppPage extends BasePage {

  protected AppPage(Page page) {
    super(page);
  }

  /** The title the page shows under the header, for example "Your Cart". */
  protected abstract String expectedTitle();

  public Header header() {
    return new Header(page);
  }

  public BurgerMenu menu() {
    return new BurgerMenu(page);
  }

  public Locator title() {
    return page.getByTestId("title");
  }

  @Override
  public void assertIsOpen() {
    super.assertIsOpen();
    assertThat(title()).hasText(expectedTitle());
  }
}
