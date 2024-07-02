package org.example.drivers.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ViewportSize;
import org.example.utils.MethodUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;

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
        LinkedHashMap<String, Integer> position =
                (LinkedHashMap<String, Integer>)page.evaluate(
                "() => { return { x: window.screenX, y: window.screenY }; }");
        return new Point(position.get("x"), position.get("y"));
    }

    @Override
    public void setPosition(Point targetPosition) {
        page.evaluate(String.format("() => { window.move(%d, %d); }",
                targetPosition.x, targetPosition.y));
    }

    @Override
    public void maximize() {
        MethodUtils.throwMethodNotImplementedException("Window.maximize()");
    }

    @Override
    public void minimize() {
        MethodUtils.throwMethodNotImplementedException("Window.minimize()");
    }

    @Override
    public void fullscreen() {
        MethodUtils.throwMethodNotImplementedException("Window.fullscreen()");
    }
}
