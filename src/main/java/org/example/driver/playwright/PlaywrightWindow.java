package org.example.driver.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ViewportSize;
import org.example.utils.MethodManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class PlaywrightWindow implements WebDriver.Window {

    private final Page page;

    /**
     * The Playwright window constructor.
     * @param page The Playwright page instance.
     */
    public PlaywrightWindow(Page page) {
        this.page = page;
    }

    @Override
    public Dimension getSize() {
        ViewportSize viewportSize = page.viewportSize();
        return new Dimension(viewportSize.width, viewportSize.height);
    }

    @Override
    public void setSize(Dimension targetSize) {
        page.setViewportSize(targetSize.width, targetSize.height);
    }

    @Override
    public Point getPosition() {
        MethodManager.throwMethodNotImplementedException("Window.getPosition()");
        return null;
    }

    @Override
    public void setPosition(Point targetPosition) {
        MethodManager.throwMethodNotImplementedException("Window.setPosition(Point targetPosition)");
    }

    @Override
    public void maximize() {
        MethodManager.throwMethodNotImplementedException("Window.maximize()");
    }

    @Override
    public void minimize() {
        MethodManager.throwMethodNotImplementedException("Window.minimize()");
    }

    @Override
    public void fullscreen() {
        MethodManager.throwMethodNotImplementedException("Window.fullscreen()");
    }
}
