package com.saucedemo.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.Proxy;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.playwright.AllurePlaywright;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Shared Playwright lifecycle for the test classes: one browser per class, a fresh context and page
 * per test, and a trace and screenshot kept only when a test fails.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

  protected static final String BASE_URL = "https://www.saucedemo.com";
  protected static final String VALID_USERNAME = "standard_user";
  protected static final String VALID_PASSWORD = "secret_sauce";

  private static final Path RESULTS_DIR = Path.of("test-results");

  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;
  private boolean failed;
  protected Page page;

  /** Records whether the test body threw, before the {@code @AfterEach} methods run. */
  @RegisterExtension
  final AfterTestExecutionCallback failureRecorder =
      extensionContext -> failed = extensionContext.getExecutionException().isPresent();

  @BeforeAll
  void launchBrowser() {
    playwright = Playwright.create();
    boolean headed = "true".equalsIgnoreCase(System.getenv("HEADED"));
    browser = browserType().launch(new BrowserType.LaunchOptions().setHeadless(!headed));
  }

  @BeforeEach
  void createContextAndPage() {
    failed = false;
    Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
    configureProxy(contextOptions);
    context = browser.newContext(contextOptions);
    context
        .tracing()
        .start(
            new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(false));
    page = context.newPage();
    AllurePlaywright.register(page);
  }

  @AfterEach
  void closeContext(TestInfo testInfo) {
    if (failed) {
      saveFailureArtifacts(artifactName(testInfo));
    } else {
      context.tracing().stop();
    }
    context.close();
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

  /** Setup for every test class except {@code LoginTests}, which tests the login itself. */
  protected void loginAsStandardUser() {
    LoginPage loginPage = new LoginPage(page);
    loginPage.navigate();
    loginPage.login(VALID_USERNAME, VALID_PASSWORD);
  }

  private BrowserType browserType() {
    String name = System.getProperty("browser", "chromium").toLowerCase(Locale.ROOT);
    return switch (name) {
      case "chromium" -> playwright.chromium();
      case "firefox" -> playwright.firefox();
      case "webkit" -> playwright.webkit();
      default ->
          throw new IllegalArgumentException(
              "Unknown browser '" + name + "'. Use chromium, firefox or webkit.");
    };
  }

  private void saveFailureArtifacts(String name) {
    Path screenshot = RESULTS_DIR.resolve("screenshots").resolve(name + ".png");
    page.screenshot(new Page.ScreenshotOptions().setPath(screenshot).setFullPage(true));

    Path trace = RESULTS_DIR.resolve("traces").resolve(name + ".zip");
    context.tracing().stop(new Tracing.StopOptions().setPath(trace));
    AllurePlaywright.attachTrace("Playwright trace", trace);
    AllurePlaywright.attachScreenshot("Failure screenshot", page);
    AllurePlaywright.attachPageSource("Page source", page);
    AllurePlaywright.attachConsoleMessages("Browser console", page);
    AllurePlaywright.attachPageErrors("Page errors", page);
  }

  private static String artifactName(TestInfo testInfo) {
    String className = testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown");
    String methodName = testInfo.getTestMethod().map(Method::getName).orElse("unknown");
    return className + "_" + methodName + "_" + System.getProperty("browser", "chromium");
  }

  private static void configureProxy(Browser.NewContextOptions options) {
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
}
