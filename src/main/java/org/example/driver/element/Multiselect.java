package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The multiselect element class.
 */
public class Multiselect extends BaseElement {

    private final Select select;

    /**
     * The multiselect element constructor by its locator.
     * @param by The element locator.
     */
    public Multiselect(By by) {
        super(by);
        select = new Select(getElement());
    }

    /**
     * Selects option by its text.
     * @param text The text of the option to select.
     */
    public void selectOptionText(String text) {
        select.selectByVisibleText(text);
    }

    /**
     * Deselects option by its text.
     * @param text The text of the option to deselect.
     */
    public void deselectOptionText(String text) {
        select.deselectByVisibleText(text);
    }

    /**
     * Selects option by its value.
     * @param value The value of the option to select.
     */
    public void selectOptionValue(String value) {
        select.selectByValue(value);
    }

    /**
     * Deelects option by its value.
     * @param value The value of the option to deselect.
     */
    public void deselectOptionValue(String value) {
        select.deselectByValue(value);
    }

    /**
     * Selects option by its index.
     * @param index The index of the option to select.
     */
    public void selectOptionIndex(int index) {
        select.selectByIndex(index);
    }

    /**
     * Deselects option by its index.
     * @param index The index of the option to deselect.
     */
    public void deselectOptionIndex(int index) {
        select.deselectByIndex(index);
    }

    /**
     * Returns the firs selected option text.
     * @return The text of the first selected option.
     */
    public String getFirstSelectedOptionText() {
        return select.getFirstSelectedOption().getText();
    }

    /**
     * Returns the firs selected option value.
     * @return The value of the first selected option.
     */
    public String getFirstSelectedOptionValue() {
        return select.getFirstSelectedOption().getDomProperty("value");
    }

    /**
     * Returns text list of selected options.
     * @return The text list of the selected options.
     */
    public List<String> getAlSelectedOptionTexts() {
        return select.getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns value list of selected options.
     * @return The value list of the selected options.
     */
    public List<String> getAlSelectedOptionValues() {
        return select.getAllSelectedOptions().stream()
                .map(option -> option.getAttribute("value"))
                .collect(Collectors.toList());
    }
}
