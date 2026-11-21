package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.saucedemo.components.ProductCard;
import com.saucedemo.domain.Pricing;
import com.saucedemo.domain.Product;
import com.saucedemo.domain.SortOrder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** The product list, {@code /inventory.html}. */
public final class InventoryPage extends AppPage {

  public InventoryPage(Page page) {
    super(page);
  }

  @Override
  public String path() {
    return "/inventory.html";
  }

  @Override
  public Optional<String> expectedTitle() {
    return Optional.of("Products");
  }

  public ProductCard product(Product product) {
    return new ProductCard(page, product);
  }

  /** Every product on the page, one locator per card. */
  public Locator productCards() {
    return page.getByTestId("inventory-item");
  }

  /**
   * Chooses a sort order. The list re-sorts at once; check the result with a web-first assertion.
   */
  public InventoryPage sortBy(SortOrder order) {
    page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Sort products"))
        .selectOption(new SelectOption().setLabel(order.label()));
    return this;
  }

  /**
   * The names of the products in the order shown. Waits for the first card, so call it after the
   * page has rendered; it reads the page once and does not wait for a sort to finish.
   */
  public List<String> productNames() {
    return page.getByTestId("inventory-item-name").allInnerTexts();
  }

  /** The prices of the products in the order shown. */
  public List<BigDecimal> productPrices() {
    return page.getByTestId("inventory-item-price").allInnerTexts().stream()
        .map(Pricing::parse)
        .toList();
  }

  /** The address of each product's picture, in the order shown. */
  public List<String> imageSources() {
    return productCards().getByRole(AriaRole.IMG).all().stream()
        .map(image -> image.getAttribute("src"))
        .toList();
  }

  /** The names locator, for web-first assertions on the order. */
  public Locator productNameLabels() {
    return page.getByTestId("inventory-item-name");
  }
}
