# SauceDemo Playwright Test Automation

Automated end-to-end test suite for [saucedemo.com](https://www.saucedemo.com) built with **Playwright for Java** and **JUnit 5**.

Ported from the original [Selenium C# test suite](https://github.com/yushirm/automation-selenium-saucedemo) with improvements including modern locator strategies, automatic waiting, test isolation via browser contexts, and failure artifact capture.

## Tech Stack

| Component        | Technology                  |
|------------------|-----------------------------|
| Language         | Java 17                     |
| Test Framework   | JUnit 5 (Jupiter)           |
| Automation       | Playwright for Java          |
| Build Tool       | Maven                       |
| CI/CD            | GitHub Actions               |
| Browser          | Chromium (headless by default) |

## Prerequisites

- **Java 17 JDK** with the fix for CVE-2026-114938, such as [Amazon Corretto 17.0.21.1.2](https://github.com/corretto/corretto-17/releases/tag/17.0.21.1.2) or a later security update on the Java 17 release line.
- **Maven 3.8+**

## Getting Started

```bash
# Clone the repository
git clone https://github.com/yushirm/automation-playwright-saucedemo.git
cd automation-playwright-saucedemo

# Install Playwright browsers (first time only)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"

# Run all tests
mvn test
```

### Run in headed mode (visible browser)

```bash
HEADED=true mvn test
```

### Run a specific test class

```bash
mvn test -Dtest="LoginTests"
```

### Run a specific test method

```bash
mvn test -Dtest="LoginTests#loginWithValidCredentials_redirectsToInventory"
```

## Test Coverage

| Test Area          | Tests | Description                                          |
|--------------------|-------|------------------------------------------------------|
| Login              | 2     | Valid/invalid credential handling                     |
| Inventory          | 3     | Add/remove items, cart navigation                     |
| Cart               | 2     | Checkout flow, continue shopping                      |
| Checkout Step One  | 2     | Form submission, cancel navigation                    |
| Checkout Step Two  | 2     | Purchase completion, cancel navigation                |
| Checkout Complete  | 1     | Return to products                                    |
| **Total**          | **12**|                                                      |

## Project Structure

```
src/
├── main/java/com/saucedemo/
│   └── pages/                    # Page Object Model classes
│       ├── LoginPage.java
│       ├── InventoryPage.java
│       ├── CartPage.java
│       ├── CheckoutStepOnePage.java
│       ├── CheckoutStepTwoPage.java
│       └── CheckoutCompletePage.java
└── test/java/com/saucedemo/
    ├── base/
    │   └── BaseTest.java         # Shared test lifecycle management
    └── tests/                    # Test classes
        ├── LoginTests.java
        ├── InventoryTests.java
        ├── CartTests.java
        ├── CheckoutStepOneTests.java
        ├── CheckoutStepTwoTests.java
        └── CheckoutCompleteTests.java
```

## Key Improvements Over Original Selenium Suite

- **Test Isolation**: Fresh browser context per test (no shared state)
- **Modern Locators**: Uses `data-test` attributes and Playwright built-in locators instead of fragile CSS class/XPath selectors
- **Auto-Waiting**: Playwright automatically waits for elements — no explicit waits needed
- **Web-First Assertions**: Auto-retrying assertions via `PlaywrightAssertions.assertThat()`
- **Failure Artifacts**: Screenshots and traces captured automatically for debugging
- **Cross-Platform CI**: Runs on Ubuntu (Linux) instead of Windows-only

## CI/CD

Tests run automatically via GitHub Actions on:
- Push to `main` or `master`
- Pull requests targeting `main` or `master`

Test results and failure artifacts are uploaded as workflow artifacts (retained for 14 days).

## Failure Debugging

When tests fail, screenshots and Playwright traces are saved to `test-results/`. To view a trace:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace test-results/traces/TestName.zip"
```
