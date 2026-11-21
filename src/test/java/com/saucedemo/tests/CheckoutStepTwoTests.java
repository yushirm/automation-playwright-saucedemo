package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Customer.validCustomer;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.Product.BIKE_LIGHT;
import static com.saucedemo.domain.Product.BOLT_T_SHIRT;
import static com.saucedemo.domain.Product.ONESIE;
import static com.saucedemo.domain.Product.RED_T_SHIRT;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.Pricing;
import com.saucedemo.domain.Product;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SauceDemoTest
@DisplayName("Checkout Step Two Tests")
class CheckoutStepTwoTests {

  /** Baskets chosen so that the tax is rounded up in most and down in one. */
  static Stream<Named<List<Product>>> baskets() {
    return Stream.of(
        named("Backpack (tax $2.3992 rounds up)", List.of(BACKPACK)),
        named("Bolt T-Shirt (tax $1.2792 rounds down)", List.of(BOLT_T_SHIRT)),
        named("Onesie (tax $0.6392 rounds up)", List.of(ONESIE)),
        named("Bolt and Red T-Shirts (tax $2.5584 rounds up)", List.of(BOLT_T_SHIRT, RED_T_SHIRT)),
        named("Backpack and Bike Light", List.of(BACKPACK, BIKE_LIGHT)),
        named("All six products", List.of(Product.values())));
  }

  private static CheckoutOverviewPage overviewFor(SauceDemo app, List<Product> basket) {
    return app.shopper(STANDARD)
        .adds(basket.toArray(Product[]::new))
        .checksOutAs(validCustomer())
        .overview();
  }

  @Test
  @Scenario("OVER-07")
  @DisplayName("Clicking finish completes the purchase")
  void clickFinish_completesPurchase(SauceDemo app, Page page) {
    assertOpen(
        page,
        app.shopper(STANDARD).adds(BACKPACK).checksOutAs(validCustomer()).overview().finish());
  }

  @Test
  @Scenario("OVER-06")
  @DisplayName("Clicking cancel returns to inventory")
  void clickCancel_returnsToInventory(SauceDemo app, Page page) {
    assertOpen(
        page,
        app.shopper(STANDARD).adds(BACKPACK).checksOutAs(validCustomer()).overview().cancel());
  }

  @Test
  @Scenario("OVER-01")
  @DisplayName("The overview lists the same lines as the cart")
  void overview_listsTheCartItems(SauceDemo app) {
    CheckoutOverviewPage overview = overviewFor(app, List.of(BACKPACK, BIKE_LIGHT));

    assertThat(overview.lines()).hasCount(2);
    for (Product product : List.of(BACKPACK, BIKE_LIGHT)) {
      assertThat(overview.line(product).quantity()).hasText("1");
      assertThat(overview.line(product).name()).hasText(product.displayName());
      assertThat(overview.line(product).price()).hasText("$" + product.price());
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("baskets")
  @Scenario({"OVER-02", "OVER-03", "OVER-04"})
  @DisplayName("Item total, tax and order total add up from the catalogue prices")
  void totals_addUpFromTheCataloguePrices(List<Product> basket, SauceDemo app) {
    CheckoutOverviewPage overview = overviewFor(app, basket);

    BigDecimal itemTotal = Pricing.itemTotal(basket);
    assertEquals(itemTotal, overview.itemTotal());
    assertEquals(Pricing.tax(itemTotal), overview.tax());
    assertEquals(Pricing.total(itemTotal), overview.total());
  }

  /** The example in docs/SCENARIOS.md, written out so the helper can't agree with a bug. */
  @Test
  @Scenario({"OVER-02", "OVER-03", "OVER-04"})
  @DisplayName("Backpack and Bike Light come to $39.98, tax $3.20, total $43.18")
  void totalsForBackpackAndBikeLight_matchTheDocumentedExample(SauceDemo app) {
    CheckoutOverviewPage overview = overviewFor(app, List.of(BACKPACK, BIKE_LIGHT));

    assertEquals(new BigDecimal("39.98"), overview.itemTotal());
    assertEquals(new BigDecimal("3.20"), overview.tax());
    assertEquals(new BigDecimal("43.18"), overview.total());
  }

  @Test
  @Scenario("OVER-05")
  @DisplayName("The overview shows the payment and shipping information")
  void overview_showsPaymentAndShippingInformation(SauceDemo app) {
    CheckoutOverviewPage overview = overviewFor(app, List.of(BACKPACK));

    assertThat(overview.paymentInformation()).hasText("SauceCard #31337");
    assertThat(overview.shippingInformation()).hasText("Free Pony Express Delivery!");
  }
}
