package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CheckoutStepOnePage {

    private final Page page;

    public CheckoutStepOnePage(Page page) {
        this.page = page;
    }

    public Locator getTitle() {
        return page.locator("[data-test='title']");
    }

    public void fillForm(String firstName, String lastName, String postalCode) {
        page.getByPlaceholder("First Name").fill(firstName);
        page.getByPlaceholder("Last Name").fill(lastName);
        page.getByPlaceholder("Zip/Postal Code").fill(postalCode);
        page.locator("[data-test='continue']").click();
    }

    public void cancel() {
        page.locator("[data-test='cancel']").click();
    }
}
