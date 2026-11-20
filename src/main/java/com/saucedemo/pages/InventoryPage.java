package com.saucedemo.pages;

import com.microsoft.playwright.Page;
import com.saucedemo.components.ProductCard;
import com.saucedemo.domain.Product;

/** The product list, {@code /inventory.html}. */
public final class InventoryPage extends AppPage {

  public InventoryPage(Page page) {
    super(page);
  }

  @Override
  protected String path() {
    return "/inventory.html";
  }

  @Override
  protected String expectedTitle() {
    return "Products";
  }

  public ProductCard product(Product product) {
    return new ProductCard(page, product);
  }
}
