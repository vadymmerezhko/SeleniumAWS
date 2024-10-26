package org.example.ui.wrappers;

import com.microsoft.playwright.Locator;

/**
 * Playwright Locator parser class.
 */
public class PlaywrightLocatorParser {

    public static String locatorToString(Locator locator) {
        String locatorString = locator.toString();
        int startIndex = locatorString.indexOf("'") + 1;
        int endIndex = locatorString.lastIndexOf("'");
        return locatorString.substring(startIndex, endIndex);
    }
}
