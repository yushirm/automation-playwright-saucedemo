package com.saucedemo.support;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.Proxy;
import com.saucedemo.SauceDemo;
import io.qameta.allure.playwright.AllurePlaywright;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Locale;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * The browser fixture, in place of v1's inherited {@code BaseTest}.
 *
 * <ul>
 *   <li>One Playwright and one browser for the whole run, closed when the run ends.
 *   <li>A fresh context and page for every test, with a trace recording.
 *   <li>A screenshot and the trace kept only when the test (or its setup) failed.
 *   <li>{@link SauceDemo} and {@link Page} resolved as test and setup method parameters.
 * </ul>
 */
public final class PlaywrightExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(PlaywrightExtension.class);
  private static final Path RESULTS_DIR = Path.of("test-results");

  /** The browser shared by every test in the run. Closed by JUnit when the root store closes. */
  private record Session(Playwright playwright, Browser browser) implements AutoCloseable {
    @Override
    public void close() {
      browser.close();
      playwright.close();
    }
  }

  /** The context and page one test runs in. */
  private record TestBrowser(BrowserContext context, Page page) {}

  // The context and page outlive this method by design: they go into the test's store and are
  // closed in afterEach, by test.context().close(), which closes the page with it. PMD's
  // CloseResource rule expects them closed here.
  @SuppressWarnings("PMD.CloseResource")
  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    Browser browser = session(extensionContext).browser();
    Browser.NewContextOptions options = new Browser.NewContextOptions();
    configureProxy(options);
    BrowserContext context = browser.newContext(options);
    context
        .tracing()
        .start(
            new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(false));
    Page page = context.newPage();
    AllurePlaywright.register(page);
    extensionContext.getStore(NAMESPACE).put(TestBrowser.class, new TestBrowser(context, page));
  }

  /**
   * Runs after the test and its {@code @AfterEach} methods. The execution exception covers a
   * failure in {@code @BeforeEach} too, so a broken setup also leaves its artifacts.
   */
  @Override
  public void afterEach(ExtensionContext extensionContext) {
    TestBrowser test = testBrowser(extensionContext);
    if (extensionContext.getExecutionException().isPresent()) {
      saveFailureArtifacts(artifactName(extensionContext), test);
    } else {
      test.context().tracing().stop();
    }
    test.context().close();
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Class<?> type = parameterContext.getParameter().getType();
    return type == SauceDemo.class || type == Page.class;
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Page page = testBrowser(extensionContext).page();
    return parameterContext.getParameter().getType() == Page.class ? page : new SauceDemo(page);
  }

  private static Session session(ExtensionContext extensionContext) {
    return extensionContext
        .getRoot()
        .getStore(NAMESPACE)
        .computeIfAbsent(Session.class, key -> openSession(), Session.class);
  }

  // The browser and Playwright live for the whole run by design: they are closed in
  // Session.close(),
  // which JUnit calls when it closes the root store at the end of the run. PMD's CloseResource rule
  // expects them closed here.
  @SuppressWarnings("PMD.CloseResource")
  private static Session openSession() {
    Playwright playwright = Playwright.create();
    playwright.selectors().setTestIdAttribute("data-test");
    boolean headed = "true".equalsIgnoreCase(System.getenv("HEADED"));
    Browser browser =
        browserType(playwright).launch(new BrowserType.LaunchOptions().setHeadless(!headed));
    return new Session(playwright, browser);
  }

  private static BrowserType browserType(Playwright playwright) {
    return switch (browserName()) {
      case "chromium" -> playwright.chromium();
      case "firefox" -> playwright.firefox();
      case "webkit" -> playwright.webkit();
      default ->
          throw new IllegalArgumentException(
              "Unknown browser '" + browserName() + "'. Use chromium, firefox or webkit.");
    };
  }

  private static String browserName() {
    return System.getProperty("browser", "chromium").toLowerCase(Locale.ROOT);
  }

  private static TestBrowser testBrowser(ExtensionContext extensionContext) {
    return extensionContext.getStore(NAMESPACE).get(TestBrowser.class, TestBrowser.class);
  }

  private static void saveFailureArtifacts(String name, TestBrowser test) {
    Path screenshot = RESULTS_DIR.resolve("screenshots").resolve(name + ".png");
    test.page().screenshot(new Page.ScreenshotOptions().setPath(screenshot).setFullPage(true));

    Path trace = RESULTS_DIR.resolve("traces").resolve(name + ".zip");
    test.context().tracing().stop(new Tracing.StopOptions().setPath(trace));
    AllurePlaywright.attachTrace("Playwright trace", trace);
    AllurePlaywright.attachScreenshot("Failure screenshot", test.page());
    AllurePlaywright.attachPageSource("Page source", test.page());
    AllurePlaywright.attachConsoleMessages("Browser console", test.page());
    AllurePlaywright.attachPageErrors("Page errors", test.page());
  }

  private static String artifactName(ExtensionContext extensionContext) {
    String className = extensionContext.getTestClass().map(Class::getSimpleName).orElse("Unknown");
    String methodName = extensionContext.getTestMethod().map(Method::getName).orElse("unknown");
    return className + "_" + methodName + "_" + browserName();
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
