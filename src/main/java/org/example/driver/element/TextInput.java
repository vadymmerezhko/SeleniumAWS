package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public class TextInput extends BaseTextElement {

    public TextInput(By by) {
        super(by);
    }

    public String getValue() {
        return getElement().getAttribute("value");
    }

    public void selectAllText() {
        sendKeys(Keys.chord(Keys.CONTROL, "a"));
    }

    public void copyText() {
        sendKeys(Keys.chord(Keys.CONTROL, "c"));
    }

    public void cutText() {
        sendKeys(Keys.chord(Keys.CONTROL, "x"));
    }
}
