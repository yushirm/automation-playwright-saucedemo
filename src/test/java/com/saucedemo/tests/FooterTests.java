package com.saucedemo.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.saucedemo.domain.User.STANDARD;

import com.saucedemo.SauceDemo;
import com.saucedemo.components.Footer;
import com.saucedemo.domain.SocialNetwork;
import com.saucedemo.support.SauceDemoTest;
import com.saucedemo.support.Scenario;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SauceDemoTest
@DisplayName("Footer Tests")
class FooterTests {

  @ParameterizedTest(name = "{0}")
  @EnumSource(SocialNetwork.class)
  @Scenario("FOOT-01")
  @DisplayName("Each social link goes to Sauce Labs' page and opens in a new tab")
  void socialLink_pointsToSauceLabsAndOpensInNewTab(SocialNetwork network, SauceDemo app) {
    Footer footer = app.shopper(STANDARD).inventory().footer();

    assertThat(footer.socialLink(network)).hasAttribute("href", network.address());
    assertThat(footer.socialLink(network)).hasAttribute("target", "_blank");
  }

  @Test
  @Scenario("FOOT-02")
  @DisplayName("The copyright line names Sauce Labs and links the terms and privacy policy")
  void copyright_showsNoticeTermsAndPrivacy(SauceDemo app) {
    Footer footer = app.shopper(STANDARD).inventory().footer();

    Pattern notice =
        Pattern.compile(
            "© \\d{4} Sauce Labs\\. All Rights Reserved\\. Terms of Service \\| Privacy Policy");
    assertThat(footer.copyright()).hasText(notice);
  }
}
