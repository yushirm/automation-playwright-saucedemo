package com.saucedemo.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Marks a browser test class. Each test gets a fresh browser context, and can take a {@link
 * com.saucedemo.SauceDemo} or a {@link com.microsoft.playwright.Page} as a parameter. A test marked
 * {@link KnownIssue} is run as an expected failure.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({PlaywrightExtension.class, KnownIssueExtension.class})
public @interface SauceDemoTest {}
