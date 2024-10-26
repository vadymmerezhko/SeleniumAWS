package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.example.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;

/**
 * Base text element class.
 */
@Slf4j
public abstract class BaseTextElement extends SmartElement {

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
    public void enterText(SmartValue text) {
        DataValidationUtils.validateNotNull(text, "text");
        enterText(text.toString());
        log.debug("Text input {} value is entered to: {}", elementName, text);
    }

    /**
     * Enters element text.
     * @param text The text to enter.
     */
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void enterText(String text) {
        DataValidationUtils.validateNotNull(text, "text");
        WebElement element = getElement();
        element.clear();
        element.sendKeys(text);
        synchro.waitForAttributeValue(element, "value", text, WAIT_ELEMENT_TIMEOUT_SECONDS);
        log.debug("Text element {} value is entered to: {}", elementName, text);
    }

    /**
     * Clears the element input field.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void clear() {
        WebElement element = getElement();
        element.clear();
        log.debug("Text element {}: value is cleared.", elementName);
    }

    /**
     * Send keyboard keys to element.
     * @param keysToSend The keyboard keys.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void sendKeys(CharSequence... keysToSend) {
        DataValidationUtils.validateNotNull(keysToSend, "keysToSend");
        WebElement element = getElement();
        element.sendKeys(keysToSend);
        log.debug("Keyboard keys are sent to text element {}: {}", elementName, keysToSend);
    }

    /**
     * Pastes text from the clipboard (if any).
     */
    @RunAlone // Run this method while other @Test or SmartElement  methods do not run or wait
    public void paste() {
        sendKeys(Keys.chord(Keys.CONTROL, "v"));
        log.debug("Text from buffer is pasted to text element {}.", elementName);
    }
}
