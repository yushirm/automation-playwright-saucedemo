package com.saucedemo.domain;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * The four ways the inventory can be sorted, with the label the sort control shows and the order
 * the products should then appear in. Products that tie keep their name order, as the site does.
 */
public enum SortOrder {
  NAME_A_TO_Z("Name (A to Z)"),
  NAME_Z_TO_A("Name (Z to A)"),
  PRICE_LOW_TO_HIGH("Price (low to high)"),
  PRICE_HIGH_TO_LOW("Price (high to low)");

  private final String label;

  SortOrder(String label) {
    this.label = label;
  }

  /** The option's text in the sort control. */
  public String label() {
    return label;
  }

  /** The whole catalogue in the order this sort should show it. */
  public List<Product> expectedOrder() {
    Comparator<Product> byName = Comparator.comparing(Product::displayName);
    Comparator<Product> byPrice = Comparator.comparing(Product::price);
    Comparator<Product> order =
        switch (this) {
          case NAME_A_TO_Z -> byName;
          case NAME_Z_TO_A -> byName.reversed();
          case PRICE_LOW_TO_HIGH -> byPrice;
          case PRICE_HIGH_TO_LOW -> byPrice.reversed();
        };
    return Stream.of(Product.values()).sorted(byName).sorted(order).toList();
  }

  /** The product names in {@link #expectedOrder()}. */
  public List<String> expectedNames() {
    return expectedOrder().stream().map(Product::displayName).toList();
  }
}
