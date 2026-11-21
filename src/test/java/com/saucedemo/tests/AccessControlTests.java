package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Named.named;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.domain.LoginError;
import com.saucedemo.domain.Product;
import com.saucedemo.pages.BasePage;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductDetailPage;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SauceDemoTest
@DisplayName("Direct access when logged out")
class AccessControlTests {

  /** Every page behind the login, named by the address a visitor could type. */
  static Stream<Named<Function<Page, BasePage>>> protectedPages() {
    return Stream.of(
        named("/inventory.html", InventoryPage::new),
        named("/inventory-item.html?id=4", p -> new ProductDetailPage(p, Product.BACKPACK)),
        named("/cart.html", CartPage::new),
        named("/checkout-step-one.html", CheckoutInformationPage::new),
        named("/checkout-step-two.html", CheckoutOverviewPage::new),
        named("/checkout-complete.html", CheckoutCompletePage::new));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("protectedPages")
  @Scenario({"GUARD-01", "GUARD-02", "GUARD-03", "GUARD-04", "GUARD-05", "GUARD-06"})
  @DisplayName("A logged-out visitor sees the login page and a message naming the page")
  void openProtectedPageLoggedOut_showsLoginWithMessage(
      Function<Page, BasePage> target, SauceDemo app, Page browserPage) {
    BasePage protectedPage = target.apply(browserPage);

    protectedPage.openDirectly();

    LoginPage login = app.loginPage();
    assertThat(login.error()).hasText(LoginError.notLoggedIn(protectedPage.path()));
    assertThat(login.usernameField()).isVisible();
  }
}
