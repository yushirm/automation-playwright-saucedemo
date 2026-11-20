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
| Cart | 7 | 3 | 1 | 3 |
| Header and burger menu | 7 | 0 | 0 | 7 |
| Footer | 2 | 0 | 0 | 2 |
| Checkout: your information | 5 | 2 | 0 | 3 |
| Checkout: overview | 7 | 2 | 0 | 5 |
| Checkout: complete | 2 | 1 | 1 | 0 |
| User-specific behaviour | 15 | 0 | 0 | 15 |
| Cross-cutting | 4 | 0 | 1 | 3 |
| **Total** | **82** | **12** | **4** | **66** |

v1's 12 tests map to 12 covered scenarios and touch 4 more in passing. That is 15% fully covered, or 20% counting partial coverage.

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
| CART-07 | Cart survives logout and login | Observed: badge still shows the earlier items. Product decision needed on whether this is intended | Not covered |

## Header and burger menu

| ID | Scenario | Observed behaviour | v1 |
|---|---|---|---|
| MENU-01 | Open and close the menu | Five links: All Items, Dynamic Catalog, About, Logout, Reset App State | Not covered |
| MENU-02 | All Items | Inventory page | Not covered |
| MENU-03 | About | Links to `https://saucelabs.com/` | Not covered |
| MENU-04 | Logout | Back to `/`; protected pages refuse access afterwards | Not covered |
| MENU-05 | Reset App State | Badge cleared. Observed quirk: product buttons still say Remove until the page is reloaded | Not covered |
| MENU-06 | Dynamic Catalog | Opens a submenu of Lazy Load, Spinner and Slider demos. New since the handover was written; scope to be agreed | Not covered |
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

## Observed behaviour that needs a decision

These are not on the handover's list of deliberate defects, and they affect `standard_user`. Each needs a call on whether v2 asserts the current behaviour or records it as a defect.

1. **Checkout with an empty cart is allowed** (CART-05 with nothing added reaches step one).
2. **Cart survives logout and login** (CART-07).
3. **Reset App State leaves product buttons saying Remove until reload** (MENU-05).
4. **Dynamic Catalog** (MENU-06) is new. In scope for v2, or out?
