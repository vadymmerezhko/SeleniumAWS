package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.annotations.RunAlone;
import com.smarte2e.data.SmartValue;
import com.smarte2e.interfaces.ReadableObject;
import com.smarte2e.interfaces.WritableObject;
import com.smarte2e.ui.wrappers.SmartElement;
import com.smarte2e.utils.DataValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static com.smarte2e.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;

/**
 * Base text element class.
 */
@Slf4j
public abstract class BaseTextElement extends SmartElement implements WebElement, ReadableObject, WritableObject {

    /**
     * Base text element constructor.
     */
    public BaseTextElement() {
    }

    /**
     * Base text element constructor by its selector.
     * @param by The element selector.
     */
    public BaseTextElement(By by) {
        super(by);
    }

    /**
     * Enters element text.
     * @param text The text to enter.
     */
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void enterText(String text) {
        DataValidator.notNull(text, "text");
        clear();
        sendKeys(text);
        synchro.waitForAttributeValue(getElement(),
                "value", text, WAIT_ELEMENT_TIMEOUT_SECONDS);
        // Do not log hidden input value for security purpose.
        log.debug("{} text input element value is entered.", elementName);
    }

    /**
     * Sets base text element text value.
     * @param value The text value.
     */
    @Override
    public <T> void setValue(T value) {
        DataValidator.notNull(value, "text");
        SmartValue smartValue = new SmartValue(value);
        enterText(smartValue.toString());
        // Do not log hidden input value for security purpose.
        log.debug("{} text input value is set.", elementName);
    }

    /**
     * Gets base text element text smart value.
     * @return The text smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartValue smartValue = new SmartValue(getValueString());
        // Do not log hidden input value for security purpose.
        log.debug("{} text input smart value is returned.", elementName);
        return smartValue;
    }

    /**
     * Gets base text element text value string.
     * @return The text value string.
     */
    public String getValueString() {
        String value = getValueDomProperty();
        // Do not log hidden input value for security purpose.
        log.debug("{} text input value is returned.", elementName);
        return value;
    }

    /**
     * Clears the element input field.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void clear() {
        super.clear();
        log.debug("{} text input element value is cleared.", elementName);
    }

    /**
     * Send keyboard keys to element.
     * @param keysToSend The keyboard keys.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void sendKeys(CharSequence... keysToSend) {
        DataValidator.notNull(keysToSend, "keysToSend");
        super.sendKeys(keysToSend);
        log.debug("Keyboard keys are sent to {} text element.", elementName);
    }

    /**
     * Pastes text from the clipboard (if any).
     */
    @RunAlone // Run this method while other @Test or SmartElement  methods do not run or wait
    public void paste() {
        sendKeys(Keys.chord(Keys.CONTROL, "v"));
        // Do not log hidden input value for security purpose.
        log.debug("Text from buffer is pasted to text element {}.", elementName);
    }
}
