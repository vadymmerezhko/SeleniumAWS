package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * The text input element class.
 */
@Slf4j
public class TextInput extends SingleLineTextInput {

    /**
     * The text input constructor with auto selector.
     */
    public TextInput() {
    }

    /**
     * The text input constructor by its selector.
     * @param by The element selector.
     */
    public TextInput(By by) {
        super(by);
    }

    /**
     * Gets text input smart value.
     * @return The smart value.
     */
    @Override
    public SmartValue getValue() {
        return new SmartValue(getValueString());
    }

    /**
     * Selects all text.
     */
    @RunAlone // Run this method while other @Test or SmartElement  methods do not run or wait
    public void selectAll() {
        sendKeys(Keys.chord(Keys.CONTROL, "a"));
        log.debug("{} text input all text is selected.", elementName);
    }

    /**
     * Selects a substring in a text input element based on "from" and "to" parameters.
     * @param from The start index of the substring (can be negative).
     * @param to The end index of the substring (can be negative).
     */
    @RunAlone // Run this method while other @Test or SmartElement  methods do not run or wait
    public void selectSubstring(int from, int to) {

        try {
            // Get the current text from the input element
            String text = getValueDomProperty();
            int length = text.length();

            // Handle negative values for 'from'
            if (from < 0) {
                from = length + from;
            }
            // Handle negative values for 'to'
            if (to < 0) {
                to = length + to + 1;
            }
            // Validate 'from' and 'to' parameters
            DataValidationUtils.validateMin(from, 0, "from");
            DataValidationUtils.validateMax(to, length, "to");

            // Set focus to element
            click();
            // Move the cursor to the start position (from)
            sendKeys(Keys.HOME);  // Move to the beginning of the input

            for (int i = 0; i < from; i++) {
                sendKeys(Keys.ARROW_RIGHT);  // Move cursor to the 'from' position
            }
            // Use Keys.chord() to simulate holding Shift and pressing the Right Arrow multiple times
            String shiftAndArrows = Keys.chord(Keys.SHIFT, repeatArrowRight(to - from));
            // Send the combined key press
            sendKeys(shiftAndArrows);
            // Do not log hidden input value for security purpose.
            log.debug("{} text input substring is selected.", elementName);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot select substring in the text input.
                    Text input: %s
                    From index: %d
                    To index: %d
                    """.stripIndent(),
                    elementName, from, to), e);
        }
    }

    /**
     * Copies input text to the clipboard.
     */
    @RunAlone // Run this method while other @Test or SmartElement  methods do not run or wait
    public void copy() {
        sendKeys(Keys.chord(Keys.CONTROL, "c"));
        // Do not log hidden input value for security purpose.
        log.debug("{} text input selected text is copied to buffer.", elementName);
    }

    /**
     * Cuts text from text input to the clipboard.
     */
    @RunAlone // Run this method while other @Test or SmartElement  methods do not run or wait
    public void cut() {
        sendKeys(Keys.chord(Keys.CONTROL, "x"));
        // Do not log hidden input value for security purpose.
        log.debug("{} text input selected text is cut to buffer.", elementName);
    }

    private String repeatArrowRight(int count) {
        return String.valueOf(Keys.ARROW_RIGHT).repeat(Math.max(0, count));
    }
}