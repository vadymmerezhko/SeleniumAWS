package org.example.page;

import org.example.driver.by.SmartBy;
import org.example.driver.element.Password;
import org.example.driver.element.TextInput;
import org.example.driver.element.Textarea;

public class WebFormPage extends BasePage {

    private final TextInput textInput = new TextInput(SmartBy.inputLabelTextContains("Text input"));
    public final Password password = new Password(SmartBy.inputLabelTextContains("Password"));
    private final Textarea textarea = new Textarea(SmartBy.textareaLabelTextContains("Textarea"));

    public void enterIntoTextInput(String text) {
        textInput.enterText(text);
    }

    public void enterPassword(String text) {
        password.enterText(text);
    }

    public void enterIntoTextarea(String text) {
        textarea.enterText(text);
    }

    public String getTextInputValue() {
        return textInput.getValue();
    }

    public String getTextareaValue() {
        return textarea.getValue();
    }
}
