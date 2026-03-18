package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage {

    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public Locator getTitle() {
        return page.locator("[data-test='title']");
    }

    public void checkout() {
        page.locator("[data-test='checkout']").click();
    }

    public void continueShopping() {
        page.locator("[data-test='continue-shopping']").click();
    }
}
