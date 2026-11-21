package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clicks that should take the browser to another page.
 *
 * <p>SauceDemo sometimes drops a click that lands just after a page has rendered: the control is
 * visible, enabled and stable, the click is delivered, and nothing happens (seen in WebKit in 2 of
 * 15 runs of the Back Home click; see docs/SCENARIOS.md, "dropped clicks"). So a navigating click
 * waits for the URL to change, and if it hasn't within a short wait, clicks once more. There is no
 * sleep: each wait ends as soon as the URL changes.
 *
 * <p>A dropped click is a defect a real user would hit, so the suite tolerates it but never hides
 * it: every retry is an Allure step, tags the test {@code dropped-click}, and is counted in the
 * summary printed at the end of the run.
 */
public final class Navigation {

  private static final System.Logger LOG = System.getLogger(Navigation.class.getName());

  /**
   * How long to wait for the URL to leave the starting page before treating the click as dropped.
   * SauceDemo changes page client-side, so a click that registered moves the URL almost at once.
   */
  private static final double DROPPED_CLICK_WAIT_MS = 2_000;

  private static final String DROPPED_CLICK_TAG = "dropped-click";

  private static final List<DroppedClick> DROPPED_CLICKS = new CopyOnWriteArrayList<>();

  /** A click that was retried because the page did not change. */
  public record DroppedClick(String url, String control) {}

  private Navigation() {}

  /** Every dropped click retried so far in this run. */
  public static List<DroppedClick> droppedClicks() {
    return List.copyOf(DROPPED_CLICKS);
  }

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
      DROPPED_CLICKS.add(new DroppedClick(startUrl, control.toString()));
      tagTestOnce();
      Allure.step("Click dropped on " + startUrl + ", retried once");
      LOG.log(
          Level.WARNING,
          "Click on {0} did not leave {1}; treating it as dropped and clicking once more",
          control,
          startUrl);
      control.click();
    }
  }

  /** Tags the running test {@code dropped-click}, once however many of its clicks were dropped. */
  private static void tagTestOnce() {
    Allure.getLifecycle()
        .updateTest(test -> {
          boolean tagged =
              test.getLabels().stream()
                  .anyMatch(label ->
                      "tag".equals(label.getName()) && DROPPED_CLICK_TAG.equals(label.getValue()));
          if (!tagged) {
            test.getLabels().add(new Label().setName("tag").setValue(DROPPED_CLICK_TAG));
          }
        });
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
