package org.example.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SmartBy extends By {

    private static ConcurrentMap<Long, By> byMap = new ConcurrentHashMap<>();

    private static long getThreadId() {
        return Thread.currentThread().threadId();
    }

    public static By id(String id) {
        By by = new ById(id);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By linkText(String linkText) {
        By by = new ByLinkText(linkText);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By partialLinkText(String partialLinkText) {
        By by = new ByPartialLinkText(partialLinkText);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By name(String name) {
        By by = new ByName(name);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By tagName(String tagName) {
        By by = new ByTagName(tagName);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By xpath(String xpathExpression) {
        By by = new ByXPath(xpathExpression);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By className(String className) {
        By by = new ByClassName(className);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By cssSelector(String cssSelector) {
        By by = new ByCssSelector(cssSelector);
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By text(String text) {
        By by = new ByXPath(String.format("//*[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By textContains(String text) {
        By by = new ByXPath(String.format("//*[contains(text(),'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By buttonText(String text) {
        By by = new ByXPath(String.format("//button[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By buttonTextContains(String text) {
        By by = new ByXPath(String.format("//button[contains(text(),'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By optionText(String text) {
        By by = new ByXPath(String.format("//option[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By optionTextContains(String text) {
        By by = new ByXPath(String.format("//option[contains(text(),'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By optionValue(String value) {
        By by = new ByCssSelector(String.format("option[value='%s']", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By optionValueContains(String value) {
        By by = new ByXPath(String.format("//option[contains(@value,'%s')]", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By childElementText(String text) {
        By by = new ByXPath(String.format("./*[text()='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By childElementPartialText(String text) {
        By by = new ByXPath(String.format("./*[contains(text(),'%s')]", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By attribute(String attributeName, String attributeValue) {
        By by = new ByCssSelector(String.format("*[%s='%s']", attributeName, attributeValue));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By attributeContains(String attributeName, String attributeValue) {
        By by = new ByXPath(String.format("//*[contains(@%s,'%s')]", attributeName, attributeValue));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By inputLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/input", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By inputLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/input", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By checkboxLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/input[@type='checkbox']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By checkboxLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/input[@type='checkbox']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By radiobuttonLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/input[@type='radio']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By radiobuttonLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/input[@type='radio']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By textareaLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/textarea", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By textareaLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/textarea", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By selectLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/select", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By selectLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/select", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By selectedOptionLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/select/option[@selected='selected']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By selectedOptionLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/select/option[@selected='selected']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By datalistLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/datalist", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By datalistLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/datalist", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By selectOptionLabelText(String text, String option) {
        By by = new ByXPath(String.format("//label[text()='%s']/select/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By selectOptionLabelTextContains(String text, String option) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/select/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By datalistOptionLabelText(String text, String option) {
        By by = new ByXPath(String.format("//label[text()='%s']/datalist/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By datalistOptionLabelTextContains(String text, String option) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s)']/datalist/option[text()='%s']", text, option));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By tableLabelText(String text) {
        By by = new ByXPath(String.format("//label[text()='%s']/table", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By tableLabelTextContains(String text) {
        By by = new ByXPath(String.format("//label[contains(text(),'%s')]/table", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By inputValue(String value) {
        By by = new ByCssSelector(String.format("input[value='%s']", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By inputType(String type) {
        By by = new ByCssSelector(String.format("input[type='%s']", type));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By inputPlaceholder(String placeholder) {
        By by = new ByCssSelector(String.format("input[placeholder='%s']", placeholder));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By altText(String text) {
        By by = new ByCssSelector(String.format("*[alt='%s']", text));
        byMap.put(getThreadId(), by);
        return by;
    }

    public static By textareaValue(String value) {
        By by = new ByCssSelector(String.format("textarea[value='%s']", value));
        byMap.put(getThreadId(), by);
        return by;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        return context.findElements(byMap.get(getThreadId()));
    }
}
