package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import java.lang.System.Logger.Level;

/**
 * Clicks that should take the browser to another page.
 *
 * <p>SauceDemo sometimes drops a click that lands just after a page has rendered: the control is
 * visible, enabled and stable, the click is delivered, and nothing happens (seen in WebKit in 2 of
 * 15 runs of the Back Home click; see docs/SCENARIOS.md, "dropped clicks"). So a navigating click
 * waits for the URL to change, and if it hasn't within a short wait, clicks once more. There is no
 * sleep: each wait ends as soon as the URL changes.
 */
public final class Navigation {

  private static final System.Logger LOG = System.getLogger(Navigation.class.getName());

  /**
   * How long to wait for the URL to leave the starting page before treating the click as dropped.
   * SauceDemo changes page client-side, so a click that registered moves the URL almost at once.
   */
  private static final double DROPPED_CLICK_WAIT_MS = 2_000;

  private Navigation() {}

  /**
   * Clicks a control that changes page. If the URL is still the starting one after a short wait,
   * the click is treated as dropped and made once more. Whether the right page then opened is for
   * the caller to assert.
   *
   * <p>Not for the login button: {@code performance_glitch_user} takes about 5 s to log in, so a
   * retry there would submit the form twice.
   */
  public static void click(Page page, Locator control) {
    String startUrl = page.url();
    control.click();
    if (!urlLeaves(page, startUrl)) {
      LOG.log(
          Level.WARNING,
          "Click on {0} did not leave {1}; treating it as dropped and clicking once more",
          control,
          startUrl);
      control.click();
    }
  }

  private static boolean urlLeaves(Page page, String startUrl) {
    try {
      page.waitForURL(
          url -> !url.equals(startUrl),
          new Page.WaitForURLOptions().setTimeout(DROPPED_CLICK_WAIT_MS));
      return true;
    } catch (TimeoutError e) {
      return false;
    }
  }
}
