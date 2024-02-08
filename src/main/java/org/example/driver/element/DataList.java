package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DataList extends BaseTextElement {

    public DataList(By by) {
        super(by);
    }

    public void selectOptionByText(String text) {
        enterText(text);
    }

    public void selectOptionByIndex(int index) {
        WebElement option = getElement().findElement(By.xpath(
                String.format("..//option[%d]", index)));
        String optionText = option.getText();
        if (optionText == null || optionText.isEmpty()) {
            String optionValue = option.getDomProperty("value");
            selectOptionByText(optionValue);
        } else {
            selectOptionByText(optionText);
        }
    }

    public String getSelectedOptionText() {
        return getElement().getDomProperty("value");
    }
}
