package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import com.saucedemo.components.BurgerMenu;
import com.saucedemo.components.Footer;
import com.saucedemo.components.Header;
import java.util.Optional;

/** A page behind the login, with the shared header and a page title. */
public abstract class AppPage extends BasePage {

  protected AppPage(Page page) {
    super(page);
  }

  /** The cookie the site sets at login, named for the user it holds. */
  public static final String SESSION_COOKIE = "session-username";

  /**
   * The title the page shows under the header when it is open, for example "Your Cart". Empty for a
   * page that has none, such as the product detail page.
   */
  public abstract Optional<String> expectedTitle();

  public Header header() {
    return new Header(page);
  }

  public BurgerMenu menu() {
    return new BurgerMenu(page);
  }

  /** Reloads the page, keeping the session. */
  public void reload() {
    page.reload();
  }

  public Footer footer() {
    return new Footer(page);
  }

  /** The session cookie, or empty if the browser holds none. */
  public Optional<Cookie> sessionCookie() {
    return page.context().cookies().stream()
        .filter(cookie -> SESSION_COOKIE.equals(cookie.name))
        .findFirst();
  }

  public Locator title() {
    return page.getByTestId("title");
  }
}
