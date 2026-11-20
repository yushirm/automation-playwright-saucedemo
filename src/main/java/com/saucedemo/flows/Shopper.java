package com.saucedemo.flows;

import com.saucedemo.domain.Customer;
import com.saucedemo.domain.Product;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;

/**
 * A logged-in user, for tests that read like the journey they check, for example {@code
 * app.shopper(STANDARD).adds(BACKPACK, BIKE_LIGHT).checksOutAs(validCustomer()).finish()}.
 */
public final class Shopper {

  private final InventoryPage inventory;

  public Shopper(InventoryPage inventory) {
    this.inventory = inventory;
  }

  public InventoryPage inventory() {
    return inventory;
  }

  /** Adds each product to the cart from the inventory page. */
  public Shopper adds(Product... products) {
    for (Product product : products) {
      inventory.product(product).addToCart();
    }
    return this;
  }

  public CartPage opensCart() {
    return inventory.header().openCart();
  }

  /** Opens the cart, checks out, enters the customer's details and arrives at the overview. */
  public CheckoutOverviewPage checksOutAs(Customer customer) {
    return opensCart().checkout().fillIn(customer).continueToOverview();
  }
}
