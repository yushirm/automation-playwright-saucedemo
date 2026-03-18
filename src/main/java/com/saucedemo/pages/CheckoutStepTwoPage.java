package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CheckoutStepTwoPage {

    private final Page page;

    public CheckoutStepTwoPage(Page page) {
        this.page = page;
    }

    public Locator getTitle() {
        return page.locator("[data-test='title']");
    }

    public void finish() {
        page.locator("[data-test='finish']").click();
    }

    public void cancel() {
        page.locator("[data-test='cancel']").click();
    }
}
