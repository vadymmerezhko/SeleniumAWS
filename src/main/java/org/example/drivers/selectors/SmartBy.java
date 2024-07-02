package org.example.drivers.selectors;

import org.example.factories.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The smart By class that extends By locator class.
 */
public class SmartBy extends By {

    static final int MAX_NESTING_LEVEL = 8;

    private static final ConcurrentMap<Long, By> byMap = new ConcurrentHashMap<>();

    private static long getThreadId() {
        return Thread.currentThread().threadId();
    }

    /**
     * Returns By locator instance by element id.
     * @param id The element id.
     * @return The By locator instance.
     */
    public static By id(String id) {
        By by = new ById(id);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by link element text.
     * @param linkText The link element text.
     * @return The By locator instance.
     */
    public static By linkText(String linkText) {
        By by = new ByLinkText(linkText);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial link element text.
     * @param partialLinkText The partial link element text.
     * @return The By locator instance.
     */
    public static By partialLinkText(String partialLinkText) {
        By by = new ByPartialLinkText(partialLinkText);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by element name.
     * @param name The element name.
     * @return The By locator instance.
     */
    public static By name(String name) {
        By by = new ByName(name);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by element tag name.
     * @param tagName The element tag name.
     * @return The By locator instance.
     */
    public static By tagName(String tagName) {
        By by = new ByTagName(tagName);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by element xpath expression.
     * @param xpathExpression The element xpath expression.
     * @return The By locator instance.
     */
    public static By xpath(String xpathExpression) {
        By by = new ByXPath(xpathExpression);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by element class name.
     * @param className The element class name.
     * @return The By locator instance.
     */
    public static By className(String className) {
        By by = new ByClassName(className);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by element CSS selector.
     * @param cssSelector The element CSS selector.
     * @return The By locator instance.
     */
    public static By cssSelector(String cssSelector) {
        By by = new ByCssSelector(cssSelector);
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by element text.
     * @param text The element text.
     * @return The By locator instance.
     */
    public static By text(String text) {
        By by = new ByXPath(String.format("//*[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial element text.
     * @param text The partial element text.
     * @return The By locator instance.
     */
    public static By textContains(String text) {
        By by = new ByXPath(String.format("//*[contains(.,'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by button element text.
     * @param text The button element text.
     * @return The By locator instance.
     */
    public static By buttonText(String text) {
        By by = new ByXPath(String.format("//button[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial button element text.
     * @param text The partial button element text.
     * @return The By locator instance.
     */
    public static By buttonTextContains(String text) {
        By by = new ByXPath(String.format("//button[contains(.,'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by option element text.
     * @param text The option element text.
     * @return The By locator instance.
     */
    public static By optionText(String text) {
        By by = new ByXPath(String.format("//option[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial option element text.
     * @param text The partial option element text.
     * @return The By locator instance.
     */
    public static By optionTextContains(String text) {
        By by = new ByXPath(String.format("//option[contains(.,'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by option element value.
     * @param value The option element value.
     * @return The By locator instance.
     */
    public static By optionValue(String value) {
        By by = new ByCssSelector(String.format("option[value='%s']", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial option element value.
     * @param value The partial option element value.
     * @return The By locator instance.
     */
    public static By optionValueContains(String value) {
        By by = new ByXPath(String.format("//option[contains(@value,'%s')]", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by child element text.
     * @param text The child element text.
     * @return The By locator instance.
     */
    public static By childElementText(String text) {
        By by = new ByXPath(String.format("./*[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial child element text.
     * @param text The partial child element text.
     * @return The By locator instance.
     */
    public static By childElementPartialText(String text) {
        By by = new ByXPath(String.format("./*[contains(.,'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by element attribute.
     * @param attributeName The attribute name.
     * @param attributeValue The attribute value.
     * @return The By locator instance.
     */
    public static By attribute(String attributeName, String attributeValue) {
        By by = new ByCssSelector(String.format("*[%s='%s']", attributeName, attributeValue));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial element attribute value.
     * @param attributeName The attribute name.
     * @param attributeValue The partial attribute value.
     * @return The By locator instance.
     */
    public static By attributeContains(String attributeName, String attributeValue) {
        By by = new ByXPath(String.format("//*[contains(@%s,'%s')]", attributeName, attributeValue));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by label element text.
     * @param text The label text.
     * @return The By locator instance.
     */
    public static By labelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/*", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial label element text.
     * @param text The partial label text.
     * @return The By locator instance.
     */
    public static By labelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]/*", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by parent element text.
     * @param text The parent element text.
     * @return The By locator instance.
     */
    public static By parentText(String text) {
        By by = new ByXPath(String.format("//*[text()='%s']//*", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial parent element text.
     * @param text The partial parent element text.
     * @return The By locator instance.
     */
    public static By parentTextContains(String text) {
        By by = new ByXPath(String.format("//*[contains(.,'%s')]//*", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by sibling input element text.
     * @param text The sibling input element text.
     * @return The By locator instance.
     */
    public static By inputSiblingText(String text) {
        return elementSiblingText("input", text);
    }

    /**
     * Returns By locator instance by sibling textarea element text.
     * @param text The sibling textarea element text.
     * @return The By locator instance.
     */
    public static By textareaSiblingText(String text) {
        return elementSiblingText("textarea", text);
    }

    /**
     * Returns By locator instance by sibling button element text.
     * @param text The sibling button element text.
     * @return The By locator instance.
     */
    public static By buttonSiblingText(String text) {
        return elementSiblingText("button", text);
    }

    /**
     * Returns By locator instance by sibling select element text.
     * @param text The sibling select element text.
     * @return The By locator instance.
     */
    public static By selectSiblingText(String text) {
        return elementSiblingText("select", text);
    }

    /**
     * Returns By locator instance by sibling element tag name and its text.
     * @param elementTag The sibling elemnt tag name.
     * @param text The sibling input element text.
     * @return The By locator instance.
     */
    public static By elementSiblingText(String elementTag, String text) {
        String xpathPattern = "//*[text()='%s']//%s//" + elementTag;
        String parentPath = "..";

        for (int i = 0; i < MAX_NESTING_LEVEL; i++) {
            String xpath = String.format(xpathPattern, text, parentPath);
            By by = new ByXPath(xpath);

            if (WebDriverFactory.getDriver().findElements(by).size() == 1) {
                byMap.put(getThreadId(), by);
                return by;
            }
            parentPath += "/..";
        }
        throw new RuntimeException(String.format(
                "No %s locator is found by sibling text '%s'", elementTag, text));
    }

    /**
     * Returns By locator instance by input label text.
     * @param text The input label text.
     * @return The By locator instance.
     */
    public static By inputLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//input", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial input label text.
     * @param text The partial input label text.
     * @return The By locator instance.
     */
    public static By inputLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//input", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by checkbox label text.
     * @param text The checkbox label text.
     * @return The By locator instance.
     */
    public static By checkboxLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//input[@type='checkbox']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial checkbox label text.
     * @param text The partial checkbox label text.
     * @return The By locator instance.
     */
    public static By checkboxLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//input[@type='checkbox']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by radio button label text.
     * @param text The radia button label text.
     * @return The By locator instance.
     */
    public static By radiobuttonLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//input[@type='radio']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial radio button label text.
     * @param text The partial radia button label text.
     * @return The By locator instance.
     */
    public static By radiobuttonLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//input[@type='radio']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by textarea label text.
     * @param text The textarea label text.
     * @return The By locator instance.
     */
    public static By textareaLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//textarea", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial textarea label text.
     * @param text The partial textarea label text.
     * @return The By locator instance.
     */
    public static By textareaLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//textarea", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by select label text.
     * @param text The select label text.
     * @return The By locator instance.
     */
    public static By selectLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//select", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial select label text.
     * @param text The partial select label text.
     * @return The By locator instance.
     */
    public static By selectLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//select", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by select option label text.
     * @param text The select option label text.
     * @return The By locator instance.
     */
    public static By selectedOptionLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//select/option[@selected='selected']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial select option label text.
     * @param text The partial select option label text.
     * @return The By locator instance.
     */
    public static By selectedOptionLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//select/option[@selected='selected']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by data list label text.
     * @param text The data list label text.
     * @return The By locator instance.
     */
    public static By datalistLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//datalist", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial data list label text.
     * @param text The partial data list label text.
     * @return The By locator instance.
     */
    public static By datalistLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//datalist", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by select option text.
     * @param text The select option text.
     * @return The By locator instance.
     */
    public static By selectOptionLabelText(String text, String option) {
        By by = new ByXPath(String.format("//label[text()='%s']//select/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial select option text.
     * @param text The partial select option text.
     * @return The By locator instance.
     */
    public static By selectOptionLabelTextContains(String text, String option) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//select/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by data list option text.
     * @param text The data list option text.
     * @return The By locator instance.
     */
    public static By datalistOptionLabelText(String text, String option) {
        By by = new ByXPath(String.format("//label[text()='%s']//datalist/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial data list option text.
     * @param text The partial data list option text.
     * @return The By locator instance.
     */
    public static By datalistOptionLabelTextContains(String text, String option) {
        By by = new ByXPath(String.format("//label[contains(.,'%s)']//datalist/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by table label text.
     * @param text The table label text.
     * @return The By locator instance.
     */
    public static By tableLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']//table", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by partial table label text.
     * @param text The partial table label text.
     * @return The By locator instance.
     */
    public static By tableLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(.,'%s')]//table", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by input value.
     * @param value The input value.
     * @return The By locator instance.
     */
    public static By inputValue(String value) {
        By by = new ByCssSelector(String.format("input[value='%s']", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by input type.
     * @param type The input type.
     * @return The By locator instance.
     */
    public static By inputType(String type) {
        By by = new ByCssSelector(String.format("input[type='%s']", type));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by input placeholder.
     * @param placeholder The input placeholder.
     * @return The By locator instance.
     */
    public static By inputPlaceholder(String placeholder) {
        By by = new ByCssSelector(String.format("input[placeholder='%s']", placeholder));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by alt text.
     * @param text The al text.
     * @return The By locator instance.
     */
    public static By altText(String text) {
        By by = new ByCssSelector(String.format("*[alt='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Returns By locator instance by textarea value.
     * @param value The textarea value.
     * @return The By locator instance.
     */
    public static By textareaValue(String value) {
        By by = new ByCssSelector(String.format("textarea[value='%s']", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    /**
     * Finds elements by context.
     * @param context The context.
     * @return The list of elements.
     */
    @Override
    public List<WebElement> findElements(SearchContext context) {
        return context.findElements(byMap.get(getThreadId()));
    }
}
