package org.example.drivers.elements;

import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The data list element class.
 */
public class DataList extends BaseTextElement {

    /**
     * The data list element constructor by its locator.
     * @param by The data list locator.
     */
    public DataList(By by) {
        super(by);
    }

    /**
     * Selects data list option by its text.
     * @param text The text of the option to select.
     */
    public void selectOptionByText(String text) {
        DataValidationUtils.validateNotNull(text, this.getClass().getSimpleName(), getElement());
        enterText(text);
    }

    /**
     * Selects data list option by its index.
     * @param index The data list option index.
     */
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

    /**
     * Returns the text of the dat list selected option.
     * @return The selected option text.
     */
    public String getSelectedOptionText() {
        return getElement().getDomProperty("value");
    }
}
