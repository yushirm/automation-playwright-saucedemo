# SauceDemo Playwright Test Automation

Automated end-to-end test suite for [saucedemo.com](https://www.saucedemo.com) built with Playwright for Java and JUnit.

Ported from the original [Selenium C# test suite](https://github.com/yushirm/automation-selenium-saucedemo), with modern locators, automatic waiting, test isolation through browser contexts, and failure artifacts.

## Tech stack

| Component | Technology |
|---|---|
| Language | Java 25 (LTS) |
| Test framework | JUnit 6 (Jupiter) |
| Automation | Playwright for Java |
| Report | Allure 3, one HTML file per run |
| Formatting and static analysis | Spotless (google-java-format), Checkstyle (Google checks), PMD, Error Prone |
| Build | Maven |
| CI | GitHub Actions, on Chromium, Firefox and WebKit |

## Prerequisites

- Java 25 JDK
- Maven 3.9 or later

## Getting started

```bash
git clone https://github.com/yushirm/automation-playwright-saucedemo.git
cd automation-playwright-saucedemo
```

Install the browsers (first time only, and after a Playwright upgrade):

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium firefox webkit"
```

Run the tests in Chromium:

```bash
mvn test
```

### Choose a browser

```bash
mvn test -Dbrowser=firefox
```

`browser` is one of `chromium` (the default), `firefox` or `webkit`.

### Run with a visible browser

```bash
HEADED=true mvn test
```

### Run one class or one test

```bash
mvn test -Dtest="LoginTests"
```

```bash
mvn test -Dtest="LoginTests#loginWithValidCredentials_redirectsToInventory"
```

## Formatting and static analysis

`mvn verify` is the full check. It compiles with Error Prone (warnings fail the build), runs the tests, then checks formatting with Spotless and runs Checkstyle and PMD. Any finding fails the build.

To run the checks without the tests:

```bash
mvn verify -DskipTests
```

To fix formatting:

```bash
mvn spotless:apply
```

PMD uses the quickstart rules with one change, in `config/pmd/ruleset.xml`: test methods may be named `action_expectedOutcome`.

Checkstyle uses the Google checks with one change, in `config/checkstyle/google_checks.xml`: methods of two lines or fewer need no Javadoc.

## Report

After a test run, build the Allure report:

```bash
mvn allure:report
```

It is written as a single file, `target/site/allure-maven-plugin/index.html`, that opens directly in a browser. Each test lists its Playwright actions as steps. A failed test also carries a screenshot, the page source, the browser console and the Playwright trace.

The first run downloads a private Node.js runtime and Allure 3 into `.allure/`, which is ignored by Git.

## Failure artifacts

When a test fails, its screenshot and Playwright trace are also saved to `test-results/`. Passing tests save nothing. To open a trace:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace test-results/traces/TestName_chromium.zip"
```

## CI

GitHub Actions runs on pushes and on pull requests to `main`:

- one job for formatting and static analysis
- one test job per browser: Chromium, Firefox and WebKit

Each test job uploads its Allure report as `allure-report-<browser>`. A failed job also uploads `test-results-<browser>` with the screenshots, traces and Surefire reports. Artifacts are kept for 14 days.

Dependabot keeps the Maven dependencies and the SHA-pinned Actions up to date.

## Test coverage

77 of the 83 scenarios in [docs/SCENARIOS.md](docs/SCENARIOS.md) are covered: 64 by passing tests (the three browser rows by the CI matrix) and 13 by known-issue tests. The other six are Dynamic Catalog (out of scope), the `performance_glitch_user` timings (PR 5), and `visual_user` layout and the accessibility check (PR 6).

| Test area | Tests | Description |
|---|---:|---|
| Login | 13 | Every user, wrong and missing credentials, dismissing the error, the session cookie |
| Direct access when logged out | 6 | Each protected page refuses a visitor who is not logged in |
| Inventory | 41 | Product details, four sort orders, adding and removing, button toggle, across users |
| Product detail | 16 | Opening from name and picture, add and remove, back, unknown id |
| Cart | 14 | Lines, removing, reload, logout and login, empty-cart checkout |
| Header and menu | 18 | Menu entries, All Items, About, Logout, Reset App State, header on every page |
| Footer | 4 | Social links, copyright |
| Checkout step one | 5 | Validation messages, submit, cancel |
| Checkout step two | 11 | Lines, item total, tax and order total, payment and shipping, finish, cancel |
| Checkout complete | 3 | Confirmation, empty cart, back home |
| Deliberate defects | 21 | `problem_user`, `error_user` and `visual_user`, all known issues |
| **Total** | **152** | 23 of them known issues |

Counts are test invocations on Chromium: a parameterised test counts once per argument.

## How the tests are written

A test asks for a `SauceDemo` and reads like the journey it checks:

```java
@SauceDemoTest
class CheckoutCompleteTests {

  private Page page;
  private CheckoutCompletePage complete;

  @BeforeEach
  void placeOrder(SauceDemo app, Page page) {
    this.page = page;
    complete = app.shopper(STANDARD).adds(BACKPACK).checksOutAs(validCustomer()).confirms();
  }

  @Test
  void clickBackToHome_returnsToInventory() {
    assertOpen(page, complete.backHome());
  }
}
```

- **`@SauceDemoTest`** registers the browser fixture: a fresh context and page per test, and a trace and screenshot kept only on failure. Tests and setup methods can ask for `SauceDemo` and `Page`.
- **Page objects** live in `pages`, and the parts shared between pages in `components`. A navigating action returns the page it leads to.
- **Flows** (`flows`) string page objects together into a user's journey, such as `Shopper` and `Checkout`.
- **Domain types** replace magic strings: `User`, `Product`, `SortOrder`, `LoginError`, `CheckoutError` and `Customer`. `Pricing` works out the item total, 8% tax and order total from the catalogue. Start a customer from `validCustomer()` and change only what the test is about.
- **Locators:** `getByRole` where the accessible name is stable and meaningful, `getByTestId` (`data-test`) otherwise.
- **Assertions:** web-first only, so they retry until the page catches up.
- **Navigating clicks** go through `Navigation.click`, which retries once if the click didn't change the page. SauceDemo sometimes drops a click that lands just after a page renders; see [docs/SCENARIOS.md](docs/SCENARIOS.md). Retries are never silent: each one is an Allure step and tags the test `dropped-click`, and every run ends with "Dropped clicks retried (<browser>): N", also written to the GitHub job summary.
- **Parameterised tests** run one check across the users or products it applies to: the six accounts, the six products, the four sort orders. Where an account is broken on purpose, the test for it says so (below).
- **No fixed waits:** `NoSleepsTest` fails the build if any code calls `Thread.sleep`, `TimeUnit.sleep` or `waitForTimeout`.

### Known issues

Some SauceDemo behaviour is wrong, either on purpose (`problem_user`, `error_user`, `visual_user`) or by accident (an empty cart can be checked out, Reset App State leaves the Remove buttons). Each is a test that asserts the *correct* behaviour and carries `@KnownIssue("why")`.

- It runs every time and is never disabled.
- If it fails on an assertion, as expected, it is reported as skipped with the reason as the message, tagged `known-issue` in Allure, and listed at the end of the run as "Known issues failing as expected (<browser>): N" and in the GitHub job summary. A failure of any other kind, such as a locator that matches nothing, is a real failure.
- If it passes, the site has been fixed and the build fails, telling you to remove the annotation.

Surefire counts these as skipped: its summary has no "expected failure" status.

Tests also carry `@Scenario("LOGIN-02")`, naming the matrix rows they cover. `ScenarioCoverageTest` fails the build if a test names a row that does not exist, or a row has neither a test nor a reason in its list.

### Design notes

- **Page objects speak the UI; flows speak the domain.** A page object's methods are named for what the user sees: `CheckoutOverviewPage.finish()` clicks the Finish button. A flow's methods are named for what the user means: `Checkout.confirms()` places the order, by calling `finish()`. Both exist on purpose, so tests can read as the journey and page objects as the screen.
- **Page objects never assert.** Page objects, components and flows expose locators (`title()`, `cartBadge().count()`) and values (`url()`, `expectedTitle()`). Tests decide what is right, through `assertThat` or `PageAssertions.assertOpen`. `ObjectModelRulesTest` fails the build if code under `src/main` calls Playwright's assertions.

## Project structure

```
config/
├── checkstyle/google_checks.xml   Checkstyle rules (Google checks, one change)
└── pmd/ruleset.xml                PMD rules
src/
├── main/java/com/saucedemo/
│   ├── SauceDemo.java             Entry point: base URL, login page, shopper
│   ├── domain/                    User, Product, SortOrder, Pricing, Customer, errors
│   ├── pages/                     Page objects, BasePage, AppPage, Navigation
│   ├── components/                Header, Footer, BurgerMenu, CartBadge, ProductCard, CartLine
│   └── flows/                     Shopper, Checkout
└── test/
    ├── java/com/saucedemo/
    │   ├── support/               PlaywrightExtension, @SauceDemoTest, @KnownIssue, @Scenario,
    │   │                          PageAssertions, run summaries
    │   ├── architecture/          NoSleepsTest, ObjectModelRulesTest, ScenarioCoverageTest
    │   └── tests/                 Test classes
    └── resources/
        └── allure.properties      Allure settings
```
