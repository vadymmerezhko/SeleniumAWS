package org.example.drivers.playwright;


import com.microsoft.playwright.Page;
import org.openqa.selenium.WebDriver.Navigation;

import java.net.URL;

/**
 * The Playwright navigation class.
 */
public class PlaywrightNavigate implements Navigation {
    private final Page page;

    /**
     * Playwright navigate constructor.
     * @param page The Playwright page instance.
     */
    public PlaywrightNavigate(Page page) {
        this.page = page;
    }

    /**
     * Navigates back.
     */
    @Override
    public void back() {
        page.goBack();
    }

    /**
     * Navigates forward.
     */
    @Override
    public void forward() {
        page.goForward();
    }

    /**
     * Navigates to URL.
     * @param url The target URL string.
     */
    @Override
    public void to(String url) {
        page.navigate(url);
    }

    /**
     * Navigates to URL.
     * @param url The target URL.
     */
    @Override
    public void to(URL url) {
        page.navigate(url.toString());
    }

    /**
     * Refreshes the page.
     */
    @Override
    public void refresh() {
        page.reload();
    }
}
