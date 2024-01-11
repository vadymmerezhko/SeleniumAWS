package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public abstract class BaseTextElement extends BaseElement {

    public BaseTextElement(By by) {
        super(by);
    }

    public void enterText(String text) {
        getElement().clear();
        getElement().sendKeys(text);
    }

    public void pasteText() {
        sendKeys(Keys.chord(Keys.CONTROL, "v"));
    }
}
