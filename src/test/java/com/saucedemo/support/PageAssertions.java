package com.saucedemo.support;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Page;
import com.saucedemo.pages.AppPage;
import com.saucedemo.pages.BasePage;

/**
 * Web-first assertions about which page is open. Assertions live in test code, not page objects.
 */
public final class PageAssertions {

  private PageAssertions() {}

  /** Asserts the browser is on the expected page: its URL, and for logged-in pages its title. */
  public static void assertOpen(Page page, BasePage expected) {
    assertThat(page).hasURL(expected.url());
    if (expected instanceof AppPage appPage) {
      assertThat(appPage.title()).hasText(appPage.expectedTitle());
    }
  }
}
