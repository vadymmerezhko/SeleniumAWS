package org.example.page;

import org.example.driver.by.SmartBy;
import org.example.driver.element.*;

/**
 * Web form class.
 */
public class WebFormPage extends BasePage {

    private final TextInput textInput = new TextInput(SmartBy.inputLabelTextContains("Text input"));
    public final Password password = new Password(SmartBy.inputLabelTextContains("Password"));
    private final Textarea textarea = new Textarea(SmartBy.textareaLabelTextContains("Textarea"));
    private final Dropdown dropdown = new Dropdown(SmartBy.selectLabelTextContains("Dropdown (select)"));
    private final FileInput fileInput = new FileInput(SmartBy.inputLabelTextContains("File input"));
    private final DataList dataList = new DataList(SmartBy.inputLabelTextContains("Dropdown (datalist)"));
    private final Checkbox checkbox1 = new Checkbox(SmartBy.checkboxLabelTextContains("Checked checkbox"));
    private final Checkbox checkbox2 = new Checkbox(SmartBy.checkboxLabelTextContains("Default checkbox"));
    private final Radiobutton radiobutton1 = new Radiobutton(SmartBy.radiobuttonLabelTextContains("Checked radio"));
    private final Radiobutton radiobutton2 = new Radiobutton(SmartBy.radiobuttonLabelTextContains("Default radio"));
    private final ColorPicker colorPicker = new ColorPicker(SmartBy.inputLabelTextContains("Color picker"));
    private final DatePicker datePicker = new DatePicker(SmartBy.inputLabelTextContains("Date picker"));

    private final RangeSlider rangeSlider = new RangeSlider(SmartBy.inputLabelTextContains("Example range"));

    /**
     * Enters text to text input.
     * @param text The text to enter.
     */
    public void enterIntoTextInput(String text) {
        textInput.enterText(text);
    }

    /**
     * Enters password.
     * @param password The password to enter.
     */
    public void enterPassword(String password) {
        this.password.enterText(password);
    }

    /**
     * Enters text to text area.
     * @param text The text to enter.
     */
    public void enterIntoTextarea(String text) {
        textarea.enterText(text);
    }

    /**
     * Returns text value from text input.
     * @return The text value.
     */
    public String getTextInputValue() {
        return textInput.getValue();
    }

    /**
     * Returns text value from text area.
     * @return The text value.
     */
    public String getTextareaValue() {
        return textarea.getValue();
    }

    /**
     * Selects dropdown option by its text.
     * @param option The option text.
     */
    public void selectDropdownOption(String option) {
        dropdown.selectOptionByText(option);
    }

    /**
     * Returns dropdown selected option value.
     * @return The text value.
     */
    public String getDropdownSelectedOption() {
        return dropdown.getSelectedOptionText();
    }

    /**
     * Selects data list option by its text.
     * @param option The option text.
     */
    public void selectDataListOption(String option) {
        dataList.selectOptionByText(option);
    }

    /**
     * Selects data list option by its index.
     * @param index The option index.
     */
    public void selectDataListOption(int index) {
        dataList.selectOptionByIndex(index);
    }

    /**
     * Returns data list selected option value.
     * @return The text value.
     */
    public String getDataListSelectedOption() {
        return dataList.getSelectedOptionText();
    }

    /**
     * Enters file path into file brows input.
     * @param filePath The file path to enter.
     */
    public void enterFilePath(String filePath) {
        fileInput.enterText(filePath);
    }

    /**
     * REturns the file path from file brows input.
     * @return The file path value.
     */
    public String getFilePath() {
        return fileInput.getValue();
    }

    /**
     * Sets the first checkbox true/false value.
     * @param value The value to set.
     */
    public void setCheckbox1Value(boolean value) {
        checkbox1.setValue(value);
    }

    /**
     * Returns the first checkbox true/false value.
     * @return  The checkbox value.
     */
    public boolean getCheckbox1Value() {
        return checkbox1.isChecked();
    }

    /**
     * Sets the second checkbox true/false value.
     * @param value The value to set.
     */
    public void setCheckbox2Value(boolean value) {
        checkbox2.setValue(value);
    }

    /**
     * Returns the second checkbox true/false value.
     * @return  The checkbox value.
     */
    public boolean getCheckbox2Value() {
        return checkbox2.isChecked();
    }

    /**
     * Selects the first radio button.
     */
    public void selectRadiobutton1() {
        radiobutton1.select();
    }

    /**
     * Selects the second radio button.
     */
    public void selectRadiobutton2() {
        radiobutton2.select();
    }

    /**
     * Returns the first radio button true/false value.
     * @return  The checkbox value.
     */
    public boolean getRadiobutton1Value() {
        return radiobutton1.getValue();
    }

    /**
     * Returns the second radio button true/false value.
     * @return  The checkbox value.
     */
    public boolean getRadiobutton2Value() {
        return radiobutton2.getValue();
    }

    /**
     * Sets color picker value in format "#RRGGBB" like "#0088ff".
     * @param color The color value to set.
     */
    public void pickColor(String color) {
        colorPicker.pickColor(color);
    }

    /**
     * Returns the color picker value in format "#RRGGBB" like "#0088ff".
     * @return The color picker value.
     */
    public String getPickedColor() {
        return colorPicker.getPickedColor();
    }

    /**
     * Sets data picker value in format "mm/DD/YYYY" like "05/23/1970".
     * @param date The data value to set.
     */
    public void pickDate(String date) {
        datePicker.pickDate(date);
    }

    /**
     * Returns data picker value.
     * @return The data value.
     */
    public String getPickedDate() {
        return datePicker.getPickedDate();
    }

    /**
     * Sets range slider value.
     * @param range The range value to set.
     */
    public void setRange(int range) {
        rangeSlider.setValue(range);
    }

    /**
     * Returns range slider value.
     * @return The range value.
     */
    public int getRange() {
        return rangeSlider.getValue();
    }
 }
