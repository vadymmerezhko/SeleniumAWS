package org.example.drivers.playwright;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import org.example.utils.MethodUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;

import java.util.List;

/**
 * The Playwright target locator class.
 */
public class PlaywrightTargetLocator implements TargetLocator {
    private final PlaywrightPage playwrightPage;
    private final Page page;

    /**
     * The Playwright target locator constructor.
     * @param playwrightPage The Playwright page instance.
     */
    public PlaywrightTargetLocator (PlaywrightPage playwrightPage) {
        this.playwrightPage = playwrightPage;
        page = playwrightPage.getPage();
    }

    /**
     * Returns frame web driver by its index.
     * @param index The frame index.
     * @return The frame web driver.
     */
    @Override
    public WebDriver frame(int index) {
        List<Frame> frames = page.frames();

        if (frames.isEmpty() || frames.size() <= index) {
            throw new RuntimeException(String.format("Frame with index %d is not found.", index));
        }

        Frame frame = frames.get(index);
        return new PlaywrightDriver(new PlaywrightPage(new PlaywrightPage(page), frame.page()));
    }

    /**
     * Returns frame web driver by its name or id.
     * @param nameOrId The frame name or id.
     * @return The frame web driver.
     */
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

    /**
     * Returns frame web driver by its web element.
     * @param frameElement The frame web element.
     * @return The frame web driver.
     */
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

    /**
     * Returns parent frame web driver.
     * @return The parent frame web driver.
     */
    @Override
    public WebDriver parentFrame() {
        return new PlaywrightDriver(playwrightPage.getParentPage());
    }

    /**
     * Returns frame web driver by window name or handle.
     * @param nameOrHandle The window name or handle.
     * @return The frame web driver.
     */
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

    /**
     * Returns frame web driver by window type hint
     * @param typeHint The window type hint.
     * @return The frame web driver.
     */
    @Override
    public WebDriver newWindow(WindowType typeHint) {
        MethodUtils.throwMethodNotImplementedException("TargetLocator.newWindow(WindowType typeHint)");
        return null;
    }

    /**
     * Returns default content frame web driver.
     * @return The default content web driver.
     */
    @Override
    public WebDriver defaultContent() {
        MethodUtils.throwMethodNotImplementedException("TargetLocator.defaultContent()");
        return null;
    }

    /**
     * Returns an active element.
     * @return The active web element.
     */
    @Override
    public WebElement activeElement() {
        MethodUtils.throwMethodNotImplementedException("TargetLocator.activeElement()");
        return null;
    }

    /**
     * Returns alert instance.
     * @return The alert instance.
     */
    @Override
    public Alert alert() {
        MethodUtils.throwMethodNotImplementedException("TargetLocator.alert()");
        return null;
    }
}
