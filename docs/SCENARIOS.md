# SauceDemo scenario matrix

Every behaviour of [saucedemo.com](https://www.saucedemo.com) that this suite could reasonably check, and whether v1 (tag `v1-one-shot`) checks it.

Behaviour was observed against the live site on 20 November 2026 with a Playwright probe, not taken from memory or from Sauce Labs' documentation. SauceDemo changes from time to time (the burger menu has gained a Dynamic Catalog entry, and the footer now links to X rather than Twitter), so anything marked "observed" may drift.

Status key:

- **Covered**: a v1 test drives the behaviour and asserts the outcome.
- **Partial**: v1 passes through the behaviour but asserts only part of it.
- **Not covered**: no v1 test touches it.

The ID column is what later PRs and test names will refer to.

## Summary

| Area | Scenarios | Covered | Partial | Not covered |
|---|---:|---:|---:|---:|
| Login | 12 | 2 | 0 | 10 |
| Direct URL access when logged out | 6 | 0 | 0 | 6 |
| Inventory | 9 | 2 | 1 | 6 |
| Product detail | 6 | 0 | 0 | 6 |
| Cart | 8 | 3 | 1 | 4 |
| Header and burger menu | 7 | 0 | 0 | 7 |
| Footer | 2 | 0 | 0 | 2 |
| Checkout: your information | 5 | 2 | 0 | 3 |
| Checkout: overview | 7 | 2 | 0 | 5 |
| Checkout: complete | 2 | 1 | 1 | 0 |
| User-specific behaviour | 15 | 0 | 0 | 15 |
| Cross-cutting | 4 | 0 | 1 | 3 |
| **Total** | **83** | **12** | **4** | **67** |

v1's 12 tests map to 12 covered scenarios and touch 4 more in passing. That is 14% fully covered, or 19% counting partial coverage.

## Login

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| LOGIN-01 | `standard_user` logs in | Lands on `/inventory.html` with six products | Covered |
| LOGIN-02 | `locked_out_user` is refused | Stays on `/`, error "Epic sadface: Sorry, this user has been locked out.", inputs get the error class | Not covered |
| LOGIN-03 | `problem_user` logs in | Lands on inventory (defects follow, see USER-P) | Not covered |
| LOGIN-04 | `performance_glitch_user` logs in | Lands on inventory after a delay of about 5 s (see USER-G) | Not covered |
| LOGIN-05 | `error_user` logs in | Lands on inventory (defects follow, see USER-E) | Not covered |
| LOGIN-06 | `visual_user` logs in | Lands on inventory (defects follow, see USER-V) | Not covered |
| LOGIN-07 | Unknown username | "Epic sadface: Username and password do not match any user in this service" | Covered |
| LOGIN-08 | Known username, wrong password | Same message as LOGIN-07 | Not covered |
| LOGIN-09 | Both fields empty | "Epic sadface: Username is required" | Not covered |
| LOGIN-10 | Username only | "Epic sadface: Password is required" | Not covered |
| LOGIN-11 | Dismiss the error with its close button | Error banner removed | Not covered |
| LOGIN-12 | Session cookie set on login | `session-username` cookie, expires after about 10 minutes | Not covered |

## Direct URL access when logged out

Each protected page sends a logged-out visitor back to the login page with "Epic sadface: You can only access '<path>' when you are logged in."

| ID | Path | v1 |
|---|---|---|
| GUARD-01 | `/inventory.html` | Not covered |
| GUARD-02 | `/inventory-item.html?id=4` | Not covered |
| GUARD-03 | `/cart.html` | Not covered |
| GUARD-04 | `/checkout-step-one.html` | Not covered |
| GUARD-05 | `/checkout-step-two.html` | Not covered |
| GUARD-06 | `/checkout-complete.html` | Not covered |

## Inventory

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| INV-01 | Product list | Six products, each with name, description, price, image and button. Prices: Backpack $29.99, Bike Light $9.99, Bolt T-Shirt $15.99, Fleece Jacket $49.99, Onesie $7.99, Test.allTheThings() T-Shirt (Red) $15.99 | Not covered |
| INV-02 | Sort Name (A to Z) | Default order | Not covered |
| INV-03 | Sort Name (Z to A) | Test.allTheThings() T-Shirt (Red) first | Not covered |
| INV-04 | Sort Price (low to high) | Onesie first | Not covered |
| INV-05 | Sort Price (high to low) | Fleece Jacket first | Not covered |
| INV-06 | Add one product | Badge shows 1 | Covered (Backpack only) |
| INV-07 | Add several products | Badge counts each one | Not covered |
| INV-08 | Remove from the inventory page | Badge decrements, hidden at zero | Covered (Backpack only) |
| INV-09 | Button toggles between Add to cart and Remove | `data-test` changes from `add-to-cart-*` to `remove-*` | Partial (implied by clicking the remove button, never asserted) |

## Product detail

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| DETAIL-01 | Open from the product name | `/inventory-item.html?id=<n>`, name, description and price match the inventory card | Not covered |
| DETAIL-02 | Open from the product image | Same page as DETAIL-01 | Not covered |
| DETAIL-03 | Add to cart from detail | Badge increments, button becomes Remove | Not covered |
| DETAIL-04 | Remove from detail | Badge decrements | Not covered |
| DETAIL-05 | Back to products | Returns to inventory | Not covered |
| DETAIL-06 | Unknown id, e.g. `?id=99` | Shows "ITEM NOT FOUND" with price "$√-1" | Not covered |

## Cart

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| CART-01 | Cart icon opens the cart | `/cart.html`, title "Your Cart" | Covered |
| CART-02 | Cart lists what was added | Quantity, name, description and price per line | Partial (cart opened, contents never checked) |
| CART-03 | Remove from the cart page | Line removed, badge decrements | Not covered |
| CART-04 | Continue Shopping | Back to inventory | Covered |
| CART-05 | Checkout | `/checkout-step-one.html` | Covered |
| CART-06 | Cart survives a page reload | Badge unchanged; contents kept in `localStorage` as `cart-contents` | Not covered |
| CART-07 | Cart survives logout and login | Badge still shows the earlier items. Correct behaviour (decided in PR 1 review); asserted as it is | Not covered |
| CART-08 | Checkout with an empty cart is blocked | Known defect: the site lets an empty cart reach checkout step one. Decided in PR 1 review: a known-issue test asserts that checkout is blocked and is expected to fail, with the reason recorded | Not covered |

## Header and burger menu

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| MENU-01 | Open and close the menu | Five links: All Items, Dynamic Catalog, About, Logout, Reset App State | Not covered |
| MENU-02 | All Items | Inventory page | Not covered |
| MENU-03 | About | Links to `https://saucelabs.com/` | Not covered |
| MENU-04 | Logout | Back to `/`; protected pages refuse access afterwards | Not covered |
| MENU-05 | Reset App State | Badge cleared, and product buttons return to Add to cart. Known defect: the buttons still say Remove until the page is reloaded. Decided in PR 1 review: a known-issue test asserts the correct behaviour and is expected to fail, with the reason recorded | Not covered |
| MENU-06 | Dynamic Catalog | Opens a submenu of Lazy Load, Spinner and Slider demos. Out of scope for v2 | Not covered: added to the site Nov 2026, not yet stable |
| MENU-07 | Page title "Products" and the app logo | Present on every logged-in page | Not covered (title asserted per page, header itself never) |

## Footer

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| FOOT-01 | Social links | X (`https://x.com/saucelabs`), Facebook (`https://www.facebook.com/saucelabs`), LinkedIn (`https://www.linkedin.com/company/sauce-labs/`), all `target="_blank"` | Not covered |
| FOOT-02 | Copyright line | "© 2026 Sauce Labs. All Rights Reserved. Terms of Service \| Privacy Policy" | Not covered |

## Checkout: your information

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| INFO-01 | All fields empty | "Error: First Name is required" | Not covered |
| INFO-02 | First name only | "Error: Last Name is required" | Not covered |
| INFO-03 | First and last name only | "Error: Postal Code is required" | Not covered |
| INFO-04 | All fields valid | Overview page | Covered |
| INFO-05 | Cancel | Back to the cart | Covered |

## Checkout: overview

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| OVER-01 | Lists the cart items | Same lines as the cart | Not covered |
| OVER-02 | Item total | Sum of prices, e.g. Backpack + Bike Light = $39.98 | Not covered |
| OVER-03 | Tax | 8% of item total, rounded to the cent ($3.20 on $39.98) | Not covered |
| OVER-04 | Total | Item total + tax ($43.18) | Not covered |
| OVER-05 | Payment and shipping information shown | Present | Not covered |
| OVER-06 | Cancel | Back to inventory | Covered |
| OVER-07 | Finish | Complete page | Covered |

## Checkout: complete

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| DONE-01 | Order confirmed | "Thank you for your order!", cart badge gone | Partial (page title checked, message and empty cart not) |
| DONE-02 | Back Home | Inventory, cart empty | Covered |

## User-specific behaviour

These are SauceDemo's deliberate defects. In v2 they become explicit expected failures with a reason, never silently disabled tests.

### problem_user (USER-P)

| ID | Observed behaviour | v1 |
|---|---|---|
| USER-P1 | Every product shows the same image | Not covered |
| USER-P2 | Sorting is ignored | Not covered |
| USER-P3 | Bolt T-Shirt, Fleece Jacket and Test.allTheThings() T-Shirt cannot be added | Not covered |
| USER-P4 | Remove on the inventory page does nothing | Not covered |
| USER-P5 | Typing a last name writes it into the first name field, so checkout cannot continue | Not covered |

### error_user (USER-E)

| ID | Observed behaviour | v1 |
|---|---|---|
| USER-E1 | Sorting is ignored and throws a JavaScript error | Not covered |
| USER-E2 | The same three products as USER-P3 cannot be added ("Failed to add item to the cart.") | Not covered |
| USER-E3 | Remove fails ("Failed to remove item from cart.") | Not covered |
| USER-E4 | Last name cannot be typed, yet Continue still proceeds (validation skipped) | Not covered |
| USER-E5 | Finish does nothing and throws a JavaScript error | Not covered |

### performance_glitch_user (USER-G)

Measured over five runs each, Chromium, from this container:

| Action | standard_user | performance_glitch_user |
|---|---|---|
| Login until the first product is visible | 71 to 76 ms | 5,098 to 5,116 ms |
| Sort by price, high to low | 13 to 16 ms | 5,014 to 5,018 ms |
| Add to cart | 28 to 38 ms | 29 to 43 ms |
| Open product detail | 50 to 56 ms | 53 to 55 ms |

| ID | Observed behaviour | v1 |
|---|---|---|
| USER-G1 | Login takes about 5 s longer than for `standard_user` | Not covered |
| USER-G2 | Sorting takes about 5 s longer | Not covered |
| USER-G3 | Add to cart and product detail are not slowed | Not covered |

A 5 s delay sits well inside Playwright's default 30 s timeout, so a functional test passes slowly and reports nothing. Catching it needs a timing assertion, which is PR 5.

### visual_user (USER-V)

| ID | Observed behaviour | v1 |
|---|---|---|
| USER-V1 | Inventory shows wrong prices (e.g. Backpack $70.16 against $29.99), while checkout charges the real prices | Not covered |
| USER-V2 | Layout differences visible only to a screenshot comparison | Not covered |

USER-V1 can be caught by a functional test that compares the inventory price with the cart price. USER-V2 needs the visual baselines in PR 6.

### locked_out_user

Covered by LOGIN-02.

## Cross-cutting

| ID | Scenario | v1 |
|---|---|---|
| X-01 | Chromium | Partial (the only browser) |
| X-02 | Firefox | Not covered |
| X-03 | WebKit | Not covered |
| X-04 | Accessibility (axe) on each page | Not covered |

## Observed site behaviour: dropped clicks

Both suites have seen SauceDemo ignore a click on a visible, enabled button that should change page. Neither case is fully explained, and they may not have the same cause, so both are recorded as observed.

### Playwright, WebKit: a click just after the page renders

`CheckoutCompleteTests.clickBackToHome_returnsToInventory`, run locally on 20 November 2026 (Playwright 1.65.1, WebKit 27.2):

| Run | WebKit | Chromium |
|---|---|---|
| As written | 2 of 15 failed | 0 of 15 failed |
| With a 500 ms pause before the click (experiment only, never committed) | 0 of 20 failed | not run |

In the failing trace:

- **Before the click:** Finish was clicked at 922 to 931 ms, and Back Home was visible, enabled and stable by 939 ms.
- **The click:** delivered inside the button's box at 970 to 974 ms.
- **After it:** no console or page error, and no navigation. The title stayed "Checkout: Complete!" for the 5 s assertion timeout.

A click that arrives within about 40 ms of the confirmation page rendering can be dropped. The trace's DOM snapshots are stored as diffs, so they don't show whether the button was re-rendered in that window.

### Selenium, Chrome: clicks and typing that never reach the page

[Automation-Selenium-SauceDemo run 44740117073](https://github.com/yushirm/Automation-Selenium-SauceDemo/actions/runs/44740117073) (Chrome 157, 20 November 2026) failed 6 of 12. Its diagnostics show:

- **The page:** fully loaded (`readyState` `complete`), with the button itself topmost at its centre, so nothing covered it.
- **The clicks:** a click recorder, a capture listener on `document`, logged no click at all for the dropped Cancel and Continue Shopping clicks.
- **The retry:** the suite's click helper waited 10 s and clicked once more, in all four affected tests, and the second click was dropped too.
- **Typing:** in two other tests, the first-name field "did not hold 'Rick' after typing".

A second attempt, retrying after 2 s instead of 10 s ([run 44749493891](https://github.com/yushirm/Automation-Selenium-SauceDemo/actions/runs/44749493891)), was dropped as well: the recorder logged neither click. Three of that run's four failures were typing that never landed. The change was reverted.

#### Is it the browser or driver version?

Investigated on 20 November 2026 from the CI logs and the runner images' published software lists. Every run used Selenium.WebDriver 4.53.0, and each runner image ships a matching Chrome and ChromeDriver.

| Run | Time (UTC) | Runner image | Chrome and ChromeDriver | Result |
|---|---|---|---|---|
| 33 (`main`) | 19 Nov 06:52 | ubuntu-26.04 20261111.196 | 157.0.8103.50 | green |
| 34, attempt 1 | 20 Nov 10:20 | 20261111.196 | 157.0.8103.50 | red, 4 of 12 |
| 34, attempt 2 | 12:12 | 20261116.202 | 157.0.8103.78 | red, 6 of 12 |
| 35, attempt 2 | 12:38 | 20261111.196 | 157.0.8103.50 | red, 2 of 12 |
| 36, attempts 1 and 2 | 12:42, 12:45 | 20261111.196 | 157.0.8103.50 | green, green |
| 37 (`main`) | 12:48 | 20261111.196 | 157.0.8103.50 | green |
| 38 | 12:48 | 20261116.88 (AWS) | not published | green |
| 39 (`main`) | 12:57 | 20261111.196 | 157.0.8103.50 | red, 7 of 12 |
| 40 | 12:57 | 20261116.202 | 157.0.8103.78 | red, 6 of 12 |
| 41 | 14:01 | 20261116.202 | 157.0.8103.78 | red, 4 of 12 |

The same image, with Chrome and ChromeDriver 157.0.8103.50, was green on 19 November and both green and red within 20 minutes on 20 November. So the Chrome rollout from .50 to .78 doesn't explain it. Every .78 run so far was red, so .78 may make it worse, but .50 fails too.

Two more things the logs show:

- **Which tests fail.** The failures are on the cart and checkout pages. Login and inventory-only tests have not failed in any run.
- **The Playwright suite at the same times.** It passed on Chromium at 12:54, 13:16 and 13:50. It uses its own bundled Chromium (build 1255, Chrome 159), not the runner's Chrome, so this shows the site taking input from a different browser at those moments. It doesn't separate the site from the browser.

### What the two have in common, and what they don't

In common: a real click on a ready button, no error, no navigation, on the cart, checkout and confirmation pages.

The difference: in WebKit, a 500 ms pause before the click stopped the failures (0 of 20). Under Selenium in Chrome, the page ignored both clicks and typing for at least 10 s. That looks less like a click arriving too early and more like the page not taking input for a while. The cause is still unknown.

#### How the Playwright suite treats it: tolerated, and reported

A dropped click is a defect a real user would hit, so the suite tolerates it without hiding it. `Navigation.click` clicks, waits up to 2 s for the URL to change, and if it hasn't, clicks once more; the page assertion then decides. There are no sleeps. Every retry is reported in three places:

- **Allure:** a step "Click dropped on <url>, retried once", and the tag `dropped-click` on the test.
- **The end of the run:** a log line "Dropped clicks retried (<browser>): N", listing each control and page. It is printed even when N is 0.
- **GitHub Actions:** the same count and list in each test job's summary.

Observed so far (Playwright 1.65.1, WebKit 27.2, 20 November 2026):

| Browser | Runs | Clicks dropped and retried | Tests that then failed |
|---|---|---|---|
| WebKit | 5 full runs | 1 (Back Home on `checkout-complete.html`) | 0 |
| Chromium | 3 full runs | 0 | 0 |

Before the retry existed, the same Back Home click failed its test in 2 of 15 WebKit runs, and in 0 of 20 with a 500 ms pause before the click.

The retry would not have saved the Selenium runs above, where the second click was dropped too.

## Decisions on observed behaviour

These were not on the handover's list of deliberate defects, and they affect `standard_user`. Decided in the PR 1 review:

| Behaviour | Scenario | Decision |
|---|---|---|
| Checkout with an empty cart is allowed | CART-08 | Defect. Known-issue test that asserts checkout is blocked, expected to fail, with the reason |
| Cart survives logout and login | CART-07 | Correct behaviour. Asserted as it is |
| Reset App State leaves product buttons saying Remove until reload | MENU-05 | Defect. Known-issue test that asserts the buttons reset, expected to fail, with the reason |
| Dynamic Catalog | MENU-06 | Out of scope for v2: added to the site Nov 2026, not yet stable |

A known-issue test is never disabled or skipped. It runs every time and is reported as an expected failure. If the site is fixed, the test starts passing and the build says so, which is the signal to turn it into an ordinary test.
