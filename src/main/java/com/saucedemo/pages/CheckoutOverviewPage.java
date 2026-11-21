package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.components.CartLine;
import com.saucedemo.domain.Pricing;
import com.saucedemo.domain.Product;
import java.math.BigDecimal;
import java.util.Optional;

/** Checkout step two, {@code /checkout-step-two.html}: the order overview. */
public final class CheckoutOverviewPage extends AppPage {

  public CheckoutOverviewPage(Page page) {
    super(page);
  }

  @Override
  public String path() {
    return "/checkout-step-two.html";
  }

  @Override
  public Optional<String> expectedTitle() {
    return Optional.of("Checkout: Overview");
  }

  public CartLine line(Product product) {
    return new CartLine(page, product);
  }

  /** Every line of the order, one locator each. */
  public Locator lines() {
    return page.getByTestId("inventory-item");
  }

  public BigDecimal itemTotal() {
    return Pricing.parse(page.getByTestId("subtotal-label").innerText());
  }

  public BigDecimal tax() {
    return Pricing.parse(page.getByTestId("tax-label").innerText());
  }

  public BigDecimal total() {
    return Pricing.parse(page.getByTestId("total-label").innerText());
  }

  public Locator paymentInformation() {
    return page.getByTestId("payment-info-value");
  }

  public Locator shippingInformation() {
    return page.getByTestId("shipping-info-value");
  }

  /**
   * Clicks Finish once and stays here. For a user whose Finish is known to do nothing: a plain
   * click, so {@link Navigation#click} doesn't report a dropped click that is really a defect.
   */
  public CheckoutOverviewPage clickFinish() {
    button("Finish").click();
    return this;
  }

  public CheckoutCompletePage finish() {
    Navigation.click(page, button("Finish"));
    return new CheckoutCompletePage(page);
  }

  /** Goes back to the inventory, not to step one or the cart. */
  public InventoryPage cancel() {
    Navigation.click(page, button("Cancel"));
    return new InventoryPage(page);
  }
}
