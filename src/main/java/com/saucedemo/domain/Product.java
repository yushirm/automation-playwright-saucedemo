package com.saucedemo.domain;

import java.math.BigDecimal;

/** The six products in the catalogue, with the names and prices shown to a standard user. */
public enum Product {
  BACKPACK(4, "Sauce Labs Backpack", "29.99"),
  BIKE_LIGHT(0, "Sauce Labs Bike Light", "9.99"),
  BOLT_T_SHIRT(1, "Sauce Labs Bolt T-Shirt", "15.99"),
  FLEECE_JACKET(5, "Sauce Labs Fleece Jacket", "49.99"),
  ONESIE(2, "Sauce Labs Onesie", "7.99"),
  RED_T_SHIRT(3, "Test.allTheThings() T-Shirt (Red)", "15.99");

  private final int id;
  private final String displayName;
  private final BigDecimal price;

  Product(int id, String displayName, String price) {
    this.id = id;
    this.displayName = displayName;
    this.price = new BigDecimal(price);
  }

  /** The number in the product's address, {@code /inventory-item.html?id=<id>}. */
  public int id() {
    return id;
  }

  public String displayName() {
    return displayName;
  }

  public BigDecimal price() {
    return price;
  }
}
