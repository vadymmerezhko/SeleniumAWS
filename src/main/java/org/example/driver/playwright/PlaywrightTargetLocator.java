package org.example.driver.playwright;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import org.example.utils.MethodManager;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;

import java.util.List;

public class PlaywrightTargetLocator implements TargetLocator {
    private final PlaywrightPage playwrightPage;
    private final Page page;

    public PlaywrightTargetLocator (PlaywrightPage playwrightPage) {
        this.playwrightPage = playwrightPage;
        page = playwrightPage.getPage();
    }

    @Override
    public WebDriver frame(int index) {
        List<Frame> frames = page.frames();

        if (frames.isEmpty() || frames.size() <= index) {
            throw new RuntimeException(String.format("Frame with index %d is not found.", index));
        }

        Frame frame = frames.get(index);
        return new PlaywrightDriver(new PlaywrightPage(new PlaywrightPage(page), frame.page()));
    }

    @Override
    public WebDriver frame(String nameOrId) {
        List<Frame> frames = page.frames();

        for (Frame frame : frames) {
            if (frame.name().equals(nameOrId)) {
                return new PlaywrightDriver(new PlaywrightPage(new PlaywrightPage(page), frame.page()));
            }
        }

        List<Locator> frameLocators = page.locator("frame").all();
        for (Locator frameLocator : frameLocators) {
            if (frameLocator.getAttribute("id").equals(nameOrId)) {
                return new PlaywrightDriver(new PlaywrightPage(new PlaywrightPage(page), frameLocator.page()));
            }
        }
        throw new RuntimeException(String.format("Frame with name or id '%s' is not found.", nameOrId));
    }

    @Override
    public WebDriver frame(WebElement frameElement) {
        if (frameElement == null) {
            throw new RuntimeException("Frame with web element in null.");
        }
        Rectangle rect = frameElement.getRect();
        List<Frame> frames = page.frames();

        for (Frame frame : frames) {
            List<Locator> locators = frame.locator("*").all();
            for (Locator locator : locators) {
                BoundingBox boundingBox = locator.boundingBox();

                if (rect.x == boundingBox.x && rect.y == boundingBox.y &&
                        rect.width == boundingBox.width && rect.height == boundingBox.height) {
                    return new PlaywrightDriver(new PlaywrightPage(new PlaywrightPage(page), locator.page()));
                }
            }
        }
        throw new RuntimeException(String.format("Frame with element %s is not found.", frameElement));
    }

    @Override
    public WebDriver parentFrame() {
        return new PlaywrightDriver(playwrightPage.getParentPage());
    }

    @Override
    public WebDriver window(String nameOrHandle) {
        List<Page> pages = page.context().pages();

        for (Page pageItem : pages) {
            if (page.title().equals(nameOrHandle)) {
                return new PlaywrightDriver(new PlaywrightPage(pageItem));
            }
        }
        throw new RuntimeException(String.format("Window with title %s is not found.", nameOrHandle));
    }

    @Override
    public WebDriver newWindow(WindowType typeHint) {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    @Override
    public WebDriver defaultContent() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    @Override
    public WebElement activeElement() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    @Override
    public Alert alert() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }
}
