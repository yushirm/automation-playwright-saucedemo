package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.domain.Product;
import com.saucedemo.pages.Navigation;
import com.saucedemo.pages.ProductDetailPage;

/** One product on the inventory page, found by its exact name. */
public final class ProductCard {

  private final Page page;
  private final Product product;
  private final Locator card;

  /** The card for one product on the page the browser is on. */
  public ProductCard(Page page, Product product) {
    this.page = page;
    this.product = product;
    this.card = Cards.forProduct(page, product);
  }

  public ProductCard addToCart() {
    addButton().click();
    return this;
  }

  public ProductCard remove() {
    removeButton().click();
    return this;
  }

  public Locator addButton() {
    return button("Add to cart");
  }

  public Locator removeButton() {
    return button("Remove");
  }

  public Locator name() {
    return card.getByTestId("inventory-item-name");
  }

  public Locator description() {
    return card.getByTestId("inventory-item-desc");
  }

  public Locator price() {
    return card.getByTestId("inventory-item-price");
  }

  public Locator image() {
    return card.getByRole(AriaRole.IMG);
  }

  /** Opens the product's page from its name. */
  public ProductDetailPage openFromName() {
    Navigation.click(page, detailLink().filter(hasInside(page.getByTestId("inventory-item-name"))));
    return new ProductDetailPage(page, product);
  }

  /** Opens the product's page from its picture. */
  public ProductDetailPage openFromImage() {
    Navigation.click(page, detailLink().filter(hasInside(page.getByRole(AriaRole.IMG))));
    return new ProductDetailPage(page, product);
  }

  /**
   * The name and the picture are both buttons named "View details for ...", so they are told apart
   * by what is inside them.
   */
  private Locator detailLink() {
    return card.getByRole(
        AriaRole.BUTTON,
        new Locator.GetByRoleOptions().setName("View details for " + product.displayName()));
  }

  private static Locator.FilterOptions hasInside(Locator inner) {
    return new Locator.FilterOptions().setHas(inner);
  }

  private Locator button(String name) {
    return card.getByRole(
        AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(name).setExact(true));
  }
}
