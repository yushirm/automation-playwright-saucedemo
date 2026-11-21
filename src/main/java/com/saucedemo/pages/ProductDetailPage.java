package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.Product;
import java.util.Optional;

/**
 * One product's own page, {@code /inventory-item.html?id=<n>}. It has no title under the header,
 * and an address that includes the product's id.
 */
public final class ProductDetailPage extends AppPage {

  private final int id;

  public ProductDetailPage(Page page, Product product) {
    this(page, product.id());
  }

  /** A product page by id, for an id the catalogue doesn't have. */
  public ProductDetailPage(Page page, int id) {
    super(page);
    this.id = id;
  }

  @Override
  public String path() {
    return "/inventory-item.html";
  }

  @Override
  public String url() {
    return SauceDemo.BASE_URL + path() + "?id=" + id;
  }

  @Override
  public Optional<String> expectedTitle() {
    return Optional.empty();
  }

  public Locator name() {
    return page.getByTestId("inventory-item-name");
  }

  public Locator description() {
    return page.getByTestId("inventory-item-desc");
  }

  public Locator price() {
    return page.getByTestId("inventory-item-price");
  }

  public Locator image() {
    return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(imageName()));
  }

  public Locator addButton() {
    return button("Add to cart");
  }

  public Locator removeButton() {
    return button("Remove");
  }

  public ProductDetailPage addToCart() {
    addButton().click();
    return this;
  }

  public ProductDetailPage remove() {
    removeButton().click();
    return this;
  }

  public InventoryPage backToProducts() {
    Navigation.click(page, button("Back to products"));
    return new InventoryPage(page);
  }

  private String imageName() {
    return name().innerText();
  }
}
