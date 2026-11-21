package com.saucedemo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;

/**
 * How an order adds up: the item total, 8% tax rounded to the cent, and the order total. The tests
 * work these out from the catalogue prices and compare them with what the overview page shows.
 */
public final class Pricing {

  /** The tax rate SauceDemo applies to the item total. */
  public static final BigDecimal TAX_RATE = new BigDecimal("0.08");

  private Pricing() {}

  public static BigDecimal itemTotal(Product... products) {
    return itemTotal(Arrays.asList(products));
  }

  public static BigDecimal itemTotal(Collection<Product> products) {
    return products.stream().map(Product::price).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public static BigDecimal tax(BigDecimal itemTotal) {
    return itemTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
  }

  public static BigDecimal total(BigDecimal itemTotal) {
    return itemTotal.add(tax(itemTotal));
  }

  /** Reads an amount such as {@code "$29.99"} or {@code "Item total: $39.98"}. */
  public static BigDecimal parse(String text) {
    int dollar = text.indexOf('$');
    if (dollar < 0) {
      throw new IllegalArgumentException("No amount in '" + text + "'");
    }
    return new BigDecimal(text.substring(dollar + 1).trim());
  }
}
