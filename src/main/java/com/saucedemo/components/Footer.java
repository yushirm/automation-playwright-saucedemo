package com.saucedemo.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.saucedemo.domain.SocialNetwork;

/** The footer of every page behind the login: the social links and the copyright line. */
public final class Footer {

  private final Page page;

  public Footer(Page page) {
    this.page = page;
  }

  public Locator socialLink(SocialNetwork network) {
    return page.getByRole(
        AriaRole.LINK, new Page.GetByRoleOptions().setName(network.linkText()).setExact(true));
  }

  public Locator copyright() {
    return page.getByTestId("footer-copy");
  }
}
