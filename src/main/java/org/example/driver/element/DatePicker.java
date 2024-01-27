package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public class DatePicker extends BaseElement {
    public DatePicker(By by) {
        super(by);
    }

    public void pickDate(String date) {
        getElement().sendKeys(date, Keys.ESCAPE);
    }

    public String getPickedDate() {
        return getElement().getAttribute("value");
    }
}
