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

public class PlaywrightManage implements Options {
    private Page page;

    public PlaywrightManage(Page page) {
        this.page = page;
    }

    @Override
    public void addCookie(Cookie cookie) {
        List<com.microsoft.playwright.options.Cookie> cookies = new ArrayList<>();
        com.microsoft.playwright.options.Cookie newCookie =
                new com.microsoft.playwright.options.Cookie(cookie.getName(), cookie.getValue());
        newCookie.url = cookie.getPath();
        cookies.add(newCookie);
        page.context().addCookies(cookies);
    }

    @Override
    public void deleteCookieNamed(String name) {
        List<com.microsoft.playwright.options.Cookie> cookies =
                (page.context().cookies()).stream().filter(cookie ->
                        cookie.name != name).collect(Collectors.toList());
        page.context().clearCookies();
        page.context().addCookies(cookies);
    }

    @Override
    public void deleteCookie(Cookie cookieToDelete) {
        List<com.microsoft.playwright.options.Cookie> cookies =
                (page.context().cookies()).stream().filter(cookie ->
                cookie.name != cookieToDelete.getName()).collect(Collectors.toList());
        page.context().clearCookies();
        page.context().addCookies(cookies);
    }

    @Override
    public void deleteAllCookies() {
        page.context().clearCookies();
    }

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

    @Override
    public WebDriver.Timeouts timeouts() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    @Override
    public WebDriver.Window window() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    @Override
    public Logs logs() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }
}
