package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class Multiselect extends BaseElement {

    private final Select select;

    public Multiselect(By by) {
        super(by);
        select = new Select(getElement());
    }

    public void selectOptionText(String text) {
        select.selectByVisibleText(text);
    }

    public void deselectOptionText(String text) {
        select.deselectByVisibleText(text);
    }

    public void selectOptionValue(String value) {
        select.selectByValue(value);
    }

    public void deselectOptionValue(String value) {
        select.deselectByValue(value);
    }

    public void selectOptionIndex(int index) {
        select.selectByIndex(index);
    }

    public void deselectOptionIndex(int index) {
        select.deselectByIndex(index);
    }

    public String getFirstSelectedOptionText() {
        return select.getFirstSelectedOption().getText();
    }

    public String getFirstSelectedOptionValue() {
        return select.getFirstSelectedOption().getAttribute("value");
    }

    public List<String> getAlSelectedOptionTexts() {
        return select.getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getAlSelectedOptionValues() {
        return select.getAllSelectedOptions().stream()
                .map(option -> option.getAttribute("value"))
                .collect(Collectors.toList());
    }

}
