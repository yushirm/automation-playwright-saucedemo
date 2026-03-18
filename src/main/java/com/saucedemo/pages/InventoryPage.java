package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class InventoryPage {

    private final Page page;

    public InventoryPage(Page page) {
        this.page = page;
    }

    public Locator getTitle() {
        return page.locator("[data-test='title']");
    }

    public void addBackpackToCart() {
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
    }

    public void removeBackpackFromCart() {
        page.locator("[data-test='remove-sauce-labs-backpack']").click();
    }

    public Locator getCartBadge() {
        return page.locator("[data-test='shopping-cart-badge']");
    }

    public void openCart() {
        page.locator("[data-test='shopping-cart-link']").click();
    }
}
