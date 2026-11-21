package com.saucedemo.domain;

/** The social links in the footer, with where each one goes. */
public enum SocialNetwork {
  X("X", "https://x.com/saucelabs"),
  FACEBOOK("Facebook", "https://www.facebook.com/saucelabs"),
  LINKEDIN("LinkedIn", "https://www.linkedin.com/company/sauce-labs/");

  private final String linkText;
  private final String address;

  SocialNetwork(String linkText, String address) {
    this.linkText = linkText;
    this.address = address;
  }

  public String linkText() {
    return linkText;
  }

  public String address() {
    return address;
  }
}
