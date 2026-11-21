package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.Product.BACKPACK;
import static com.saucedemo.domain.User.STANDARD;
import static com.saucedemo.support.PageAssertions.assertOpen;
import static org.junit.jupiter.api.Named.named;

import com.microsoft.playwright.Page;
import com.saucedemo.SauceDemo;
import com.saucedemo.components.BurgerMenu;
import com.saucedemo.components.BurgerMenu.Item;
import com.saucedemo.domain.LoginError;
import com.saucedemo.domain.Product;
import com.saucedemo.pages.AppPage;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductDetailPage;
import com.saucedemo.support.KnownIssue;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

@SauceDemoTest
@DisplayName("Header and Burger Menu Tests")
class MenuTests {

  /** Every page behind the login, opened straight from its address. */
  static Stream<Named<Function<Page, AppPage>>> loggedInPages() {
    return Stream.of(
        named("Inventory", InventoryPage::new),
        named("Product detail", p -> new ProductDetailPage(p, BACKPACK)),
        named("Cart", CartPage::new),
        named("Checkout: your information", CheckoutInformationPage::new),
        named("Checkout: overview", CheckoutOverviewPage::new),
        named("Checkout: complete", CheckoutCompletePage::new));
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(Item.class)
  @Scenario("MENU-01")
  @DisplayName("Opening the menu shows each entry")
  void openMenu_showsEntry(Item item, SauceDemo app) {
    BurgerMenu menu = app.shopper(STANDARD).inventory().menu().open();

    assertThat(menu.item(item)).isVisible();
  }

  @Test
  @Scenario("MENU-01")
  @DisplayName("Closing the menu hides its entries")
  void closeMenu_hidesEntries(SauceDemo app) {
    BurgerMenu menu = app.shopper(STANDARD).inventory().menu().open();
    assertThat(menu.item(Item.LOGOUT)).isVisible();

    menu.close();

    assertThat(menu.item(Item.LOGOUT)).hasCount(0);
  }

  @Test
  @Scenario("MENU-02")
  @DisplayName("All Items goes to the inventory from another page")
  void clickAllItems_goesToInventory(SauceDemo app, Page page) {
    CartPage cart = app.shopper(STANDARD).opensCart();

    assertOpen(page, cart.menu().open().allItems());
  }

  @Test
  @Scenario("MENU-03")
  @DisplayName("About links to the Sauce Labs site")
  void aboutLink_pointsToSauceLabs(SauceDemo app) {
    BurgerMenu menu = app.shopper(STANDARD).inventory().menu().open();

    assertThat(menu.aboutLink()).hasAttribute("href", "https://saucelabs.com/");
  }

  @Test
  @Scenario("MENU-04")
  @DisplayName("Logout returns to the login page")
  void clickLogout_returnsToLoginPage(SauceDemo app, Page page) {
    LoginPage login = app.shopper(STANDARD).inventory().menu().open().logout();

    assertOpen(page, login);
    assertThat(login.usernameField()).isVisible();
  }

  @Test
  @Scenario("MENU-04")
  @DisplayName("After logging out, a protected page is refused")
  void afterLogout_protectedPageIsRefused(SauceDemo app, Page page) {
    LoginPage login = app.shopper(STANDARD).inventory().menu().open().logout();
    InventoryPage inventory = new InventoryPage(page);

    inventory.openDirectly();

    assertThat(login.error()).hasText(LoginError.notLoggedIn(inventory.path()));
  }

  @Test
  @Scenario("MENU-05")
  @DisplayName("Reset App State empties the cart badge")
  void resetAppState_clearsCartBadge(SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).adds(BACKPACK, Product.ONESIE).inventory();
    assertThat(inventory.header().cartBadge().count()).hasText("2");

    inventory.menu().open().resetAppState();

    assertThat(inventory.header().cartBadge().count()).isHidden();
  }

  /**
   * After Reset App State the badge is empty but the buttons still say Remove until the page is
   * reloaded. Decided in the PR 1 review to be a defect.
   */
  @Test
  @Scenario("MENU-05")
  @KnownIssue("Reset App State clears the cart but leaves the product buttons saying Remove")
  @DisplayName("Reset App State puts the product buttons back to Add to cart")
  void resetAppState_resetsProductButtons(SauceDemo app) {
    InventoryPage inventory = app.shopper(STANDARD).adds(BACKPACK).inventory();
    assertThat(inventory.product(BACKPACK).removeButton()).isVisible();

    inventory.menu().open().resetAppState();

    assertThat(inventory.product(BACKPACK).addButton()).isVisible();
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("loggedInPages")
  @Scenario("MENU-07")
  @DisplayName("Every logged-in page has the logo, the menu button and the cart")
  void header_showsLogoMenuAndCart(Function<Page, AppPage> target, SauceDemo app, Page page) {
    app.shopper(STANDARD);
    AppPage appPage = target.apply(page);

    appPage.openDirectly();

    assertOpen(page, appPage);
    assertThat(appPage.header().logo()).isVisible();
    assertThat(appPage.menu().openButton()).isVisible();
    assertThat(appPage.header().cartLink()).isVisible();
  }
}
