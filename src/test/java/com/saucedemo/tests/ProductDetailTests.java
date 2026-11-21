package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.components.ProductCard;
import com.saucedemo.domain.Product;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.ProductDetailPage;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SauceDemoTest
@DisplayName("Product Detail Page Tests")
class ProductDetailTests {

  @ParameterizedTest(name = "{0}")
  @EnumSource(Product.class)
  @Scenario("DETAIL-01")
  @DisplayName("Opening a product from its name shows the same details as the inventory")
  void openFromName_showsTheInventoryDetails(Product product, SauceDemo app, Page page) {
    ProductCard card = app.shopper(STANDARD).inventory().product(product);
    String description = card.description().innerText();

    ProductDetailPage detail = card.openFromName();

    assertOpen(page, detail);
    assertThat(detail.name()).hasText(product.displayName());
    assertThat(detail.description()).hasText(description);
    assertThat(detail.price()).hasText("$" + product.price());
    assertThat(detail.image()).isVisible();
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(Product.class)
  @Scenario("DETAIL-02")
  @DisplayName("Opening a product from its picture opens the same page")
  void openFromImage_opensTheSamePage(Product product, SauceDemo app, Page page) {
    ProductDetailPage detail = app.shopper(STANDARD).inventory().product(product).openFromImage();

    assertOpen(page, detail);
    assertThat(detail.name()).hasText(product.displayName());
  }

  @Test
  @Scenario("DETAIL-03")
  @DisplayName("Adding from the detail page shows one on the cart badge and offers Remove")
  void addToCart_incrementsBadgeAndOffersRemove(SauceDemo app) {
    ProductDetailPage detail = app.shopper(STANDARD).inventory().product(BACKPACK).openFromName();

    detail.addToCart();

    assertThat(detail.header().cartBadge().count()).hasText("1");
    assertThat(detail.removeButton()).isVisible();
  }

  @Test
  @Scenario("DETAIL-04")
  @DisplayName("Removing from the detail page hides the cart badge and offers Add to cart")
  void remove_hidesBadgeAndOffersAddToCart(SauceDemo app) {
    ProductDetailPage detail = app.shopper(STANDARD).inventory().product(BACKPACK).openFromName();
    detail.addToCart();
    assertThat(detail.header().cartBadge().count()).hasText("1");

    detail.remove();

    assertThat(detail.header().cartBadge().count()).isHidden();
    assertThat(detail.addButton()).isVisible();
  }

  @Test
  @Scenario("DETAIL-05")
  @DisplayName("Back to products returns to the inventory")
  void clickBackToProducts_returnsToInventory(SauceDemo app, Page page) {
    ProductDetailPage detail = app.shopper(STANDARD).inventory().product(BACKPACK).openFromName();

    InventoryPage inventory = detail.backToProducts();

    assertOpen(page, inventory);
  }

  /**
   * SauceDemo shows an "ITEM NOT FOUND" page with a price of "$√-1" for an id it doesn't have. That
   * is what the site does, so it is asserted as observed rather than marked as a defect.
   */
  @Test
  @Scenario("DETAIL-06")
  @DisplayName("An unknown product id shows ITEM NOT FOUND")
  void openUnknownProduct_showsItemNotFound(SauceDemo app, Page page) {
    app.shopper(STANDARD);
    ProductDetailPage detail = new ProductDetailPage(page, 99);

    detail.openDirectly();

    assertThat(detail.name()).hasText("ITEM NOT FOUND");
    assertThat(detail.price()).hasText("$√-1");
  }
}
