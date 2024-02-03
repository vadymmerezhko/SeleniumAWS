package org.example.driver.element;

import org.example.driver.playwright.PlaywrightElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class DatePicker extends BaseElement {
    public DatePicker(By by) {
        super(by);
    }

    public void pickDate(String date) {
        WebElement element = getElement();
        if (element instanceof PlaywrightElement) {
            ((PlaywrightElement)element).setValue(date);
            return;
        }
        element.sendKeys(date, Keys.ESCAPE);
    }

    public String getPickedDate() {
        return getElement().getAttribute("value");
    }
}
