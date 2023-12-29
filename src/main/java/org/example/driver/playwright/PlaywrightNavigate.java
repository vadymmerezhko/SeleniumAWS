package org.example.driver.playwright;


import com.microsoft.playwright.Page;
import org.openqa.selenium.WebDriver.Navigation;

import java.net.URL;

public class PlaywrightNavigate implements Navigation {
    private final Page page;

    public PlaywrightNavigate(Page page) {
        this.page = page;
    }

    @Override
    public void back() {
        page.goBack();
    }

    @Override
    public void forward() {
        page.goForward();
    }

    @Override
    public void to(String url) {
        page.navigate(url);
    }

    @Override
    public void to(URL url) {
        page.navigate(url.toString());
    }

    @Override
    public void refresh() {
        page.reload();
    }
}
