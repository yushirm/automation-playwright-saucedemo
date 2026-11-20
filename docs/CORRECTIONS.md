# Corrections log

A running record of what the AI got wrong during v2, kept per PR so that the list in IMPROVEMENTS.md is taken from here and not written from memory at the end.

There are two kinds of entry. "Corrected by Yushir" is what review caught. "Caught by the AI itself" is what the AI found and fixed before review. Keeping them apart shows what the review step added.

## Fixes the port needed after it landed

The one-shot port in #1 needed these fixes after it landed, before v2 started.

| PR | What the port left | What Yushir changed |
|---|---|---|
| #2 | CI installed Temurin 17 through `setup-java@v4` on `ubuntu-latest`, with no check for a known security fix | Corretto 17 with `check-latest`, a CI step that fails below 17.0.21.1, `setup-java` pinned by SHA, and a fixed Ubuntu 24.04 runner |
| #3 | `checkout` and `upload-artifact` referenced by floating major tag, no Dependabot, no licence file | Both pinned by SHA, Dependabot for Actions, a rights notice in `LICENSE` |
| #7 | Workflow token had default permissions | Read-only `contents` permission |
| #8 | README carried a one-shot disclaimer | Removed after Yushir reviewed the suite |

## Corrected by Yushir

| PR | What the AI did | Correction | Why |
|---|---|---|---|
| 1 | Reported "0 role-based locators" as a metric and listed "locators are not role-based" as a finding, implying `getByRole` should replace v1's `data-test` locators | Reworded the metric and finding. The v2 rule is `getByRole` where the accessible name is stable and meaningful, `getByTestId` otherwise | SauceDemo's `data-test` ids are stable, and Playwright recommends test ids where they exist. A missing role locator is not a gap in itself |
| 2 | Met Checkstyle's MissingJavadocMethod by writing Javadoc for all 19 flagged methods and constructors, most of which restated the name (`checkout()`: "Clicks Checkout to go to checkout step one") | `minLineCount=2` set in a local copy of Checkstyle 14.4.0's `google_checks.xml` (`config/checkstyle/`), the only change to it. Javadoc that repeated the name deleted. 5 method comments kept, each saying something the name doesn't (what `fillForm` submits, where each Cancel goes, what `login` leads to, which classes use `loginAsStandardUser`) | A comment that restates the method name is noise to read and to maintain. Short methods with clear names need none |
| 3 | Said Selenium #11 (the upload-artifact pin, in the Selenium repo) was blocked from merging by required status checks while CI was red, without checking the repository's ruleset | The `main` ruleset requires a PR and blocks force-push and deletion, but has no required status checks, so #11 could merge | Claims about what blocks a merge come from the ruleset itself, not from assumption |
| 3 | Made the dropped-click retry report itself only as a `WARNING` line in the Surefire log, where nobody would see it on a green run | Every retry is now an Allure step ("Click dropped on <url>, retried once") and tags the test `dropped-click`. A count with the list of clicks is printed at the end of every run, and written to the GitHub job summary | A dropped click is a defect a real user would hit. The suite can tolerate it, but it must never hide it |

## Caught by the AI itself

| PR | Slip | How it was caught | Fix |
|---|---|---|---|
| 1 | Scenario totals in SCENARIOS.md written as 83 scenarios and 67 not covered, when the rows added up to 82 and 66 | Counted the matrix rows with a script before committing | Totals corrected. (A later review decision added a row, so the totals are now 83 and 67 for a different reason) |
| 1 | Wrote that `visual_user`'s inventory prices "change between visits", having watched a single visit | Re-read the probe output against the claim | Claim removed; only the observed wrong prices are recorded |
| 1 | Described what PR #2 changed from its file list alone, without reading the diff | Read the diff before committing | Entry rewritten to match the diff: Temurin via `setup-java@v4` on `ubuntu-latest`, replaced by Corretto with `check-latest`, a minimum-version step, a SHA pin and Ubuntu 24.04 |
| 1 | A live-site probe counted elements before the page had rendered, and briefly suggested the Selenium suite's locators had gone from the site | The same probe printed the page HTML a moment later, showing `#first-name` present | Locators confirmed present; the Selenium CI failure was traced to a timing race instead |
| 2 | Wrote the PMD ruleset's reference to the quickstart rules as a garbled path (`category/java/../rulesets/java/quickstart.xml`) | Re-read the file before the first run | Changed to `rulesets/java/quickstart.xml` |
| 2 | Wrote `m -> m.getName()` in the new `BaseTest` | The new PMD gate failed the build on it (`LambdaCanBeMethodReference`) | Changed to `Method::getName` |
| 2 | Assumed allure-playwright's failure hook would attach a screenshot and page source for registered pages | Ran a deliberately failing test in a scratch copy and read the Allure results: only the trace and console were there, most likely because the hook runs after `@AfterEach` has closed the context | `BaseTest` attaches the screenshot and page source itself before closing the context |
| 3 | Located product cards with `Pattern.quote(name)` as an exact-match regex | Ran the ported suite: 9 of 13 tests timed out finding the card. Playwright runs patterns as JavaScript regexes, which don't support Java's `\Q...\E` | Exact text through `getByTestId(...).and(getByText(name, exact))`, no regex |
| 3 | Probed the live site's locators with `count()`, which doesn't wait, and got zeros for elements that were there (the same slip as in PR 1) | Zeros for elements known to exist, such as the inventory title | The probe waits for each element before counting |
| 3 | Assumed the password field had no textbox role and planned a test-id locator for it | The probe resolved `getByRole(TEXTBOX, "Password")` to one element: the field has an `aria-label` | Role locator, as for the username |
| 3 | Named the test-data starting point `aCustomer()` | Checkstyle's Google method-name rule failed the build on it | Renamed `validCustomer()`, which also says what makes it special: it passes checkout validation |
| 3 | Wrote the dropped-click log message as "left the page on" when it meant "did not leave" | Re-read before the first run | Message corrected |
