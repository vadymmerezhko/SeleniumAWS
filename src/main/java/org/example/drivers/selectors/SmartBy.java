package org.example.drivers.selectors;

import org.example.exceptions.SmartRuntimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SmartBy extends By {
    private By by;
    private final String text;
    private String elementName;

    /**
     * Returns selector found by nest order:
     * - Algorithm;
     * - OpenAI;
     * - Image.
     * @return The selector.
     */
    public static SmartBy magic() {
        return new SmartBy();
    }

    /**
     * Returns selector with keyword found by nest order:
     * - Algorithm;
     * - OpenAI;
     * - Image.
     * @param keyword The keyword.
     * @return The selector.
     */
    public static SmartBy magicKeyword(String keyword) {
        return new SmartBy(keyword);
    }

    private SmartBy() {
        super();
        this.text = null;
    }

    private SmartBy(String keyword) {
        super();
        this.text = keyword;
    }

    /**
     * Returns wrapped By selector.
     * @return The By selector.
     */
    public By getBy() {
        return by;
    }

    /**
     * Sets wrapped By selector.
     * @param by The By selector.
     */
    public void setBy(By by) {
        this.by = by;
    }

    /**
     * Returns text identifier string.
     * @return The text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets element name.
     * @param elementName The element name.
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Returns element name.
     * @return The element name.
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * Finds web elements.
     * @param context The context.
     * @return The list of found elements.
     */
    @Override
    public List<WebElement> findElements(SearchContext context) {
        if (by == null) {
            throw new SmartRuntimeException("By selector is NULL.");
        }
        return by.findElements(context);
    }

    /**
     * Converts ByAI object to string.
     * @return The string.
     */
    @Override
    public String toString() {
        return by.toString();
    }
}
