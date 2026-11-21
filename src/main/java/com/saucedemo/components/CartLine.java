package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.domain.Product;

/** One line of the cart, or of the order overview, which lists the same lines without Remove. */
public final class CartLine {

  private final Locator line;

  public CartLine(Page page, Product product) {
    this.line = Cards.forProduct(page, product);
  }

  public Locator quantity() {
    return line.getByTestId("item-quantity");
  }

  public Locator name() {
    return line.getByTestId("inventory-item-name");
  }

  public Locator description() {
    return line.getByTestId("inventory-item-desc");
  }

  public Locator price() {
    return line.getByTestId("inventory-item-price");
  }

  public Locator removeButton() {
    return line.getByRole(
        AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Remove").setExact(true));
  }
}
