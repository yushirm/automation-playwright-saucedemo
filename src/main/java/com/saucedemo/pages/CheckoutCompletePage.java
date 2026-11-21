package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.Optional;

/** The order confirmation page, {@code /checkout-complete.html}. */
public final class CheckoutCompletePage extends AppPage {

  public CheckoutCompletePage(Page page) {
    super(page);
  }

  @Override
  public String path() {
    return "/checkout-complete.html";
  }

  @Override
  public Optional<String> expectedTitle() {
    return Optional.of("Checkout: Complete!");
  }

  /** The confirmation heading, "Thank you for your order!". */
  public Locator heading() {
    return page.getByTestId("complete-header");
  }

  public Locator message() {
    return page.getByTestId("complete-text");
  }

  public InventoryPage backHome() {
    Navigation.click(page, button("Back Home"));
    return new InventoryPage(page);
  }
}
