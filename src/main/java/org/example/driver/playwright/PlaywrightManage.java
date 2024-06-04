package org.example.driver.playwright;

import com.microsoft.playwright.Page;
import org.example.utils.MethodManager;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.logging.Logs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Playwright manage class.
 */
public class PlaywrightManage implements Options {
    private final Page page;

    /**
     * The Playwright manage constructor.
     * @param page The Playwright page instance.
     */
    public PlaywrightManage(Page page) {
        this.page = page;
    }

    /**
     * Adds cookie.
     * @param cookie The cookie to add.
     */
    @Override
    public void addCookie(Cookie cookie) {
        List<com.microsoft.playwright.options.Cookie> cookies = new ArrayList<>();
        com.microsoft.playwright.options.Cookie newCookie =
                new com.microsoft.playwright.options.Cookie(cookie.getName(), cookie.getValue());
        newCookie.url = cookie.getPath();
        cookies.add(newCookie);
        page.context().addCookies(cookies);
    }

    /**
     * Delete cookie by its name.
     * @param name The name of the cookie to delete.
     */
    @Override
    public void deleteCookieNamed(String name) {
        List<com.microsoft.playwright.options.Cookie> cookies =
                (page.context().cookies()).stream().filter(cookie ->
                        !cookie.name.equals(name)).collect(Collectors.toList());
        page.context().clearCookies();
        page.context().addCookies(cookies);
    }

    /**
     * Deletes cookie.
     * @param cookieToDelete The cookie to delete.
     */
    @Override
    public void deleteCookie(Cookie cookieToDelete) {
        List<com.microsoft.playwright.options.Cookie> cookies =
                (page.context().cookies()).stream().filter(cookie ->
                !cookie.name.equals(cookieToDelete.getName())).collect(Collectors.toList());
        page.context().clearCookies();
        page.context().addCookies(cookies);
    }

    /**
     * Deletes all cookies.
     */
    @Override
    public void deleteAllCookies() {
        if (page != null) {
            page.context().clearCookies();
        }
    }

    /**
     * Returns all cookies.
     * @return The list of cookies.
     */
    @Override
    public Set<Cookie> getCookies() {
        return page.context().cookies().stream()
                .map(cookie ->  {
                    String protocol = cookie.secure ? "https" : "http";
                    return new Cookie(cookie.name, cookie.value,
                        String.format("%s://%s%s", protocol, cookie.domain, cookie.path));
                })
                .collect(Collectors.toSet());
    }

    /**
     * Returns cookie by its name.
     * @param name The cookie name.
     * @return The cookie.
     */
    @Override
    public Cookie getCookieNamed(String name) {
        List <com.microsoft.playwright.options.Cookie> cookies =
                (page.context().cookies()).stream().filter(cookie ->
                        cookie.name.equals(name)).toList();
        com.microsoft.playwright.options.Cookie targetCookie = cookies.get(0);
        String protocol = targetCookie.secure ? "https" : "http";
        return new Cookie(targetCookie.name, targetCookie.value,
                String.format("%s://%s%s", protocol, targetCookie.domain, targetCookie.path));
    }

    /**
     * Returns timeouts.
     * @return The timeouts instance.
     */
    @Override
    public WebDriver.Timeouts timeouts() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    /**
     * Return window instance.
     * @return The window instance.
     */
    @Override
    public WebDriver.Window window() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    /**
     * Returns logs instance.
     * @return The log instance.
     */
    @Override
    public Logs logs() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }
}
