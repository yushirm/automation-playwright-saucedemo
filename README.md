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

| Test area | Tests | Description |
|---|---|---|
| Login | 2 | Valid and invalid credentials |
| Inventory | 3 | Add and remove items, cart navigation |
| Cart | 2 | Checkout, continue shopping |
| Checkout step one | 2 | Form submission, cancel |
| Checkout step two | 2 | Finish, cancel |
| Checkout complete | 1 | Back to products |
| **Total** | **12** | |

[docs/SCENARIOS.md](docs/SCENARIOS.md) maps every SauceDemo behaviour against these tests, and [docs/AUDIT.md](docs/AUDIT.md) sets out the plan for widening them.

## Project structure

```
config/pmd/ruleset.xml            PMD rules
src/
├── main/java/com/saucedemo/
│   └── pages/                    Page objects
│       ├── LoginPage.java
│       ├── InventoryPage.java
│       ├── CartPage.java
│       ├── CheckoutStepOnePage.java
│       ├── CheckoutStepTwoPage.java
│       └── CheckoutCompletePage.java
└── test/
    ├── java/com/saucedemo/
    │   ├── base/
    │   │   └── BaseTest.java     Browser lifecycle and failure artifacts
    │   └── tests/                Test classes
    └── resources/
        └── allure.properties     Allure settings
```
