package org.example.pages;

import org.example.data.SmartClass;
import org.example.drivers.elements.*;

/**
 * Web form class.
 */
public class WebFormPage extends SmartPage {

    private final TextInput textInput = new TextInput();
    private final Password password = new Password();
    private final Textarea textarea = new Textarea();
    private final Dropdown dropdown = new Dropdown();
    private final FileInput fileInput = new FileInput();
    private final DataList dataList = new DataList();
    private final Checkbox checkbox1 = new Checkbox();
    private final Checkbox checkbox2 = new Checkbox();
    private final Radiobutton radiobutton1 = new Radiobutton();
    private final Radiobutton radiobutton2 = new Radiobutton();
    private final ColorPicker colorPicker = new ColorPicker();
    private final DatePicker datePicker = new DatePicker();
    private final RangeSlider rangeSlider = new RangeSlider();
    private final Button submitButton = new Button();

    /**
     * WebFormPage constructor that initializes web elements.
     */
    public WebFormPage() {
        super();
        initialize();
    }

    /**
     * Enters text to text input.
     * @param text The text to enter.
     */
    public void enterIntoTextInput(SmartClass text) {
        textInput.enterText(text.toString());
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
    public void enterIntoTextarea(SmartClass text) {
        textarea.enterText(text.toString());
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
    public void selectDropdownOption(SmartClass option) {
        dropdown.selectOptionByText(option.toString());
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
    public void selectDataListOption(SmartClass option) {
        dataList.selectOptionByText(option.toString());
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
    public void enterFilePath(SmartClass filePath) {
        fileInput.enterText(filePath.toString());
    }

    /**
     * Returns the file path from file brows input.
     * @return The file path value.
     */
    public String getFilePath() {
        return fileInput.getValue();
    }

    /**
     * Sets the first checkbox true/false value.
     * @param value The value to set.
     */
    public void setCheckbox1Value(SmartClass value) {
        checkbox1.setValue(value.toBoolean());
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
    public void setCheckbox2Value(SmartClass value) {
        checkbox2.setValue(value.toBoolean());
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
    public void pickColor(SmartClass color) {
        colorPicker.pickColor(color.toString());
    }

    /**
     * Returns the color picker value in format "#RRGGBB" like "#0088ff".
     * @return The color picker value.
     */
    public String getColor() {
        return colorPicker.getPickedColor();
    }

    /**
     * Sets data picker value in format "mm/DD/YYYY" like "05/23/1970".
     * @param date The data value to set.
     */
    public void pickDate(SmartClass date) {
        datePicker.pickDate(date.toString());
    }

    /**
     * Returns data picker value.
     * @return The data value.
     */
    public String getDate() {
        return datePicker.getPickedDate();
    }

    /**
     * Sets range slider value.
     * @param range The range value to set.
     */
    public void setRange(SmartClass range) {
        rangeSlider.setValue(range.toInteger());
    }

    /**
     * Returns range slider value.
     * @return The range value.
     */
    public int getRange() {
        return rangeSlider.getValue();
    }

    /**
     * Submits the Web Form.
     */
    public void submit() {
        submitButton.click();
    }
 }
