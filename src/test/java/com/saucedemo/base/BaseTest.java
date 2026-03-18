package com.saucedemo.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.Proxy;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Path;
import java.nio.file.Paths;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    protected static final String BASE_URL = "https://www.saucedemo.com";
    protected static final String VALID_USERNAME = "standard_user";
    protected static final String VALID_PASSWORD = "secret_sauce";

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    protected Page page;

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        boolean headed = "true".equalsIgnoreCase(System.getenv("HEADED"));
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(!headed)
        );
    }

    @BeforeEach
    void createContextAndPage(TestInfo testInfo) {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        configureProxy(contextOptions);
        context = browser.newContext(contextOptions);
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(false));
        page = context.newPage();
    }

    @AfterEach
    void closeContext(TestInfo testInfo) {
        if (testInfo.getTestMethod().isPresent()) {
            String testName = testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown")
                    + "_" + testInfo.getTestMethod().get().getName();
            captureArtifacts(testName);
        }
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    protected void loginAsStandardUser() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
    }

    private void configureProxy(Browser.NewContextOptions options) {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (proxyHost == null || proxyPort == null) {
            return;
        }
        Proxy proxy = new Proxy("http://" + proxyHost + ":" + proxyPort);
        String proxyUser = System.getProperty("http.proxyUser");
        String proxyPass = System.getProperty("http.proxyPassword");
        if (proxyUser != null && proxyPass != null) {
            proxy.setUsername(proxyUser).setPassword(proxyPass);
        }
        options.setProxy(proxy);
    }

    private void captureArtifacts(String testName) {
        try {
            Path screenshotPath = Paths.get("test-results", "screenshots", testName + ".png");
            screenshotPath.getParent().toFile().mkdirs();
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));

            Path tracePath = Paths.get("test-results", "traces", testName + ".zip");
            tracePath.getParent().toFile().mkdirs();
            context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        } catch (Exception e) {
            // Best effort - don't fail the test due to artifact capture issues
        }
    }
}
