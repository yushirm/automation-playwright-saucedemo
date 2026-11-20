# Audit of v1

This is the measured starting point for v2. v1 is the Playwright for Java port of the [2022 Selenium C# suite](https://github.com/yushirm/Automation-Selenium-SauceDemo), produced by an AI in a single session and then hardened by Yushir in a few small PRs. It runs, it passes and it is easy to read. This audit records what it covers and how it is built, so that each later PR can show what it changed against the same numbers.

Everything below was measured on 20 November 2026 against `main` at `dcf4c80` ("README: remove the one-shot creation notice (#8)"), which is what the `v1-one-shot` tag will point to. No code was changed to take these measurements. Static analysis and the extra browsers were run in a throwaway copy outside the repository.

## Headline numbers

| Metric | v1 |
|---|---|
| Tests | 12 |
| Scenarios covered (of 83 in [SCENARIOS.md](SCENARIOS.md)) | 12 covered, 4 partial (14%, or 19% with partial) |
| User types exercised | 1 of 6 (`standard_user`) |
| Products exercised | 1 of 6 (Sauce Labs Backpack) |
| Browsers in CI | 1 (Chromium) |
| Pass rate over three local runs | 36 of 36 |
| Run time, full suite, warm Maven cache | 14.2 to 15.4 s (Maven total); 0.67 to 1.13 s per test |
| Hard waits (`Thread.sleep`, `waitForTimeout`) | 0 |
| Assertion calls | 16, all web-first (`PlaywrightAssertions.assertThat`) |
| Locators | 23: 18 `data-test` as hand-written CSS, 5 `getByPlaceholder` |
| Test classes repeating the same setup chain | 5 of 6 |
| Checkstyle (Google checks) | 297, of which 262 are indentation |
| PMD (quickstart) | 13, of which 12 are test-method naming |
| Error Prone (default checks) | 0 |
| Dependencies at their latest stable version | 0 of 5 build dependencies, 0 of 3 Actions |
| Lines of Java | 541 |

## How it was measured

- **Tests and run time.** `mvn test` three times in a row on Chromium. All 12 tests passed each time. Maven reported 15.4 s, 14.8 s and 14.2 s.
- **Browser binaries.** v1 pins Playwright 1.56.0, which needs Chromium build 1194. The container had a newer build only, so the first run failed before any test started. Installing the matching browsers with Playwright's own CLI fixed that. No source change was involved.
- **Cross-browser.** I edited `playwright.chromium()` to `webkit()` and then `firefox()` in a scratch copy. WebKit passed 12 of 12 unchanged. Firefox could not be measured here: every test failed with `SEC_ERROR_UNKNOWN_ISSUER` because Firefox does not trust this container's HTTPS proxy certificate. The cause is the sandbox. The GitHub-hosted runners in PR 2 will give the real Firefox result.
- **Static analysis.** Run once in a scratch copy with Checkstyle 14.4.0 (`google_checks.xml`), PMD 7.29.0 (`quickstart.xml`) and Error Prone 2.53.0. None of these were added to the build.
- **Versions.** Latest stable versions were read from Maven Central's metadata and the GitHub tags for each Action on the day, not from memory. Milestones, betas and release candidates were excluded.
- **Scenario coverage.** I probed the live site with Playwright to record what it does today, then mapped each v1 test to the scenarios it asserts. See [SCENARIOS.md](SCENARIOS.md).

## What v1 does well

v2 builds on all of this.

- **No hard waits anywhere.** Every wait is Playwright's own auto-waiting.
- **Every assertion is web-first**, so none of them race the page.
- **Stable locators.** Every element is found by `data-test` attribute or placeholder. There is no XPath and no styling class.
- **Test isolation.** Each test gets a fresh browser context, so no state leaks between tests.
- **Zero flakiness** over 36 test executions.
- **Error Prone at default settings finds nothing.**
- **It runs on WebKit unchanged**, which suggests the page objects are not leaning on Chromium quirks.
- **The CI is already hardened**: SHA-pinned Actions, read-only token, a timeout, Dependabot for Actions. Most of that came from Yushir's follow-up PRs (#2, #3, #7), recorded in [CORRECTIONS.md](CORRECTIONS.md).

## Findings, most important first

### 1. Coverage is one happy path for one user

Twelve tests, all as `standard_user`, all with the Backpack. Five of the six user types SauceDemo exists to provide are never logged in. Nor are sorting, product detail, the burger menu, the footer, checkout validation, totals and tax, or logged-out access. 67 of 83 scenarios are not touched.

The deliberate defects are the most valuable part of SauceDemo as a test target, and none of them are exercised. A suite run as `problem_user` would currently have no way to report that three products cannot be added to the cart.

### 2. Nothing checks the numbers

No test asserts a price, an item total, the 8% tax or the order total. The checkout tests assert only that the next page's title appears. A pricing regression, or `visual_user`'s wrong inventory prices (Backpack $70.16, charged $29.99), would pass.

### 3. The performance user would pass silently

`performance_glitch_user` adds about 5 s to login and to sorting (measured: 5,098 to 5,116 ms against 71 to 76 ms for `standard_user`). Playwright's default timeout is 30 s, so a functional test would pass and report nothing. Catching this needs an explicit timing assertion with a budget.

### 4. "Artifacts on failure" is really "artifacts always"

`BaseTest` starts a trace before every test and saves a screenshot and trace after every test, pass or fail. A green run wrote 12 screenshots and 12 traces, 5.5 MB in total. The README says failure artifacts. On a larger suite this makes the CI artifact noisy and slow to upload, and it hides the traces you actually want among the ones you don't.

In the same method, an empty `catch (Exception e)` swallows any error from saving them. If the screenshot fails, the trace is never stopped and nothing says so. This is the one PMD finding of substance.

### 5. Setup is copied, and the fixture is inherited

Five of six test classes start by logging in, and four of those then rebuild the same chain: add the Backpack, open the cart, check out, sometimes fill the form and finish. Each copy is 3 to 6 lines. A change to the checkout flow means editing four files.

The fixture lives in an abstract `BaseTest` that every test class extends. It holds the Playwright lifecycle, the proxy setup, the artifact capture and the test data constants all in one place. Inheritance works for one fixture. It gets awkward once some tests need a logged-in user, some a filled cart and some a different browser, which is exactly where coverage is heading. JUnit 5 extensions compose; a base class doesn't.

`BaseTest` also uses `@TestInstance(PER_CLASS)` with mutable fields for the context and page. That is fine run serially, but it blocks running tests in parallel within a class.

### 6. Magic strings and duplicated locators

- `"https://www.saucedemo.com"` is defined twice: in `BaseTest` and again inside `LoginPage.navigate()`.
- `[data-test='title']` is defined separately in five page classes.
- Customer details `"Rick"`, `"Grimes"`, `"2293"` appear three times each. Page titles (`"Products"` four times, `"Your Cart"` twice) are repeated as literals in tests.
- Credentials are constants on the base class, and there is one user, so there is nowhere obvious to add the other five.
- Product actions are per product (`addBackpackToCart()`, `removeBackpackFromCart()`). Covering six products this way would mean twelve near-identical methods.

### 7. Dependencies have fallen behind

| Dependency | v1 | Latest stable | Gap |
|---|---|---|---|
| Java (CI, `maven.compiler.source` and `target`) | 17 | 25 (current LTS) | two LTS releases |
| Playwright for Java | 1.56.0 (October 2025) | 1.65.1 | 9 minor releases |
| JUnit Jupiter | 5.11.3 (October 2024) | 6.1.3 | one major release |
| maven-compiler-plugin | 3.13.0 | 3.16.0 | 3 minor |
| maven-surefire-plugin | 3.5.2 | 3.6.1 | 1 minor |
| exec-maven-plugin | 3.4.1 | 3.7.0 | 3 minor |
| actions/checkout | v4.4.0 | v7.0.1 | 3 major |
| actions/setup-java | v4.9.1 | v6.3.0 | 2 major |
| actions/upload-artifact | v4.6.2 | v7.0.2 | 3 major |

Dependabot is configured for Actions only, and has three bumps open (#4, #5, #6). Maven dependencies are not watched at all, which is how Playwright drifted nine releases.

### 8. One browser

CI installs and runs Chromium only, and the browser is hard-coded in `BaseTest`. WebKit passes locally without changes. Firefox is unmeasured (see above).

### 9. No enforced standards

There is no formatter or linter in the build. The Checkstyle count is high mostly because v1 uses 4-space indentation and Google style uses 2. Either is fine, and Spotless with google-java-format would settle it mechanically. The remaining findings are missing Javadoc (26) and import order (9). PMD's 12 naming findings are the `method_scenario` test names, which are a reasonable convention. If we keep them, the rule should be configured to allow them rather than the names changed.

Error Prone at default severity finds nothing. With every optional check switched on it finds 38, all of them style preferences (`var`, `@NullMarked`, Javadoc).

### 10. Reporting is Surefire XML only

There is no human-readable report with steps, so a failure in CI means downloading an artifact and opening a trace. The `@DisplayName`s are good and would carry straight into a step-based report.

### 11. Locators: the right attributes, written by hand

v1 picked the right thing to locate by. SauceDemo's `data-test` ids are stable, and Playwright recommends test ids where a site provides them. The 18 `data-test` locators are written as raw CSS (`page.locator("[data-test='checkout']")`) rather than through `getByTestId` with the test-id attribute set to `data-test` once. The 5 placeholder locators would break if the placeholder text changed, though the field's purpose would not.

The v2 rule: `getByRole` where the accessible name is stable and meaningful (a button's visible label, a field's label), and `getByTestId` otherwise. Neither is preferred for its own sake. Where both work, the role locator also checks that the element is exposed as what it looks like.

## Plan for PRs 2 to 7

The handover order holds up. The audit suggests three adjustments, marked **(changed)**.

**PR 2: tooling and standards.**
Java 25 LTS, with the patched-17 instructions dropped from the README. Playwright 1.65.1, JUnit 6.1.3 and the current Maven plugins. Spotless (google-java-format) and Checkstyle, PMD and Error Prone failing the build. PMD's naming rule is tuned for test classes only, with a comment saying why, so the `method_scenario` test names stay. Dependabot for Maven as well as Actions. A CI matrix of Chromium, Firefox and WebKit. Traces and screenshots kept **on failure only** (finding 4), without the swallowed exception. An Allure report with steps, published as a CI artifact. **(changed)** PR 2 supersedes Dependabot PRs #4 to #6, which touch the same workflow file; Yushir will close them once it lands.

**PR 3: object model.**
Page objects per page. Components for Header, BurgerMenu, CartBadge and ProductCard. `User` and `Product` enums and a `Customer` builder, so there are no magic strings. JUnit 5 extensions in place of `BaseTest`. Locators follow the rule in finding 11, web-first assertions, and a check that fails the build on any sleep. Tests that read like specs. The existing 12 tests are ported first and must stay green, so the refactor is proven before coverage grows.

**PR 4: coverage.**
Fill the scenario matrix, parameterised across user types where behaviour is shared. Deliberate defects become explicit expected failures with the reason recorded. **(changed)** This PR will also check the numbers (finding 2: totals, 8% tax, and inventory price against cart price) and apply the behaviour decisions at the end of SCENARIOS.md: known-issue tests for empty-cart checkout and Reset App State, and the cart surviving logout asserted as it is.

**PR 5: performance_glitch_user.**
Timing assertions on login and sorting against an explicit budget, with the measured baseline and the reasoning for the threshold written down. The data so far: about 70 ms against about 5,100 ms, with add-to-cart and product detail unaffected. That is a wide, stable gap, so a budget can be generous and still fail cleanly.

**PR 6: visual_user and accessibility.**
Screenshot comparison against checked-in baselines, and axe checks on each page. **(changed)** One question to settle first: the Java axe binding (`com.deque.html.axe-core:playwright`, currently 4.14.0) versus injecting axe-core directly. I'll compare the two then rather than decide now.

**PR 7: the story.**
README rewrite and IMPROVEMENTS.md, with the before and after scorecard built from the same rows as the headline table above.

## Decisions from review

Recorded from Yushir's review of this PR.

1. **Empty-cart checkout** (CART-08): defect. Known-issue test asserting checkout is blocked, expected to fail, with the reason.
2. **Cart survives logout and login** (CART-07): correct behaviour. Asserted as it is.
3. **Reset App State leaving Remove buttons** (MENU-05): defect. Known-issue test, as for 1.
4. **Dynamic Catalog** (MENU-06): out of scope for v2. Listed in SCENARIOS.md as not covered: added to the site Nov 2026, not yet stable.
5. **Java 25 LTS.** The patched-17 instructions come out of the README.
6. **Allure**, published as a CI artifact.
7. **Test names stay `method_scenario`.** PMD's naming rule is tuned for test classes only, with a comment saying why.
8. **Dependabot #4 to #6**: Yushir closes them once PR 2 lands.
