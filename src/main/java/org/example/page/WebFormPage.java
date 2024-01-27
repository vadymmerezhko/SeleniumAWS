package org.example.page;

import org.example.driver.by.SmartBy;
import org.example.driver.element.*;

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

    public void selectDropdownOption(String option) {
        dropdown.selectOptionByText(option);
    }

    public String getDropdownSelectedOption() {
        return dropdown.getSelectedOptionText();
    }

    public void selectDataListOption(String option) {
        dataList.selectOptionByText(option);
    }

    public void selectDataListOption(int index) {
        dataList.selectOptionByIndex(index);
    }

    public String getDataListSelectedOption() {
        return dataList.getSelectedOptionText();
    }

    public void enterFilePath(String filePath) {
        fileInput.enterText(filePath);
    }

    public String getFilePath() {
        return fileInput.getValue();
    }

    public void setCheckbox1Value(boolean value) {
        checkbox1.setValue(value);
    }

    public boolean getCheckbox1Value() {
        return checkbox1.isChecked();
    }

    public void setCheckbox2Value(boolean value) {
        checkbox2.setValue(value);
    }

    public boolean getCheckbox2Value() {
        return checkbox2.isChecked();
    }

    public void selectRadiobutton1() {
        radiobutton1.select();
    }

    public void selectRadiobutton2() {
        radiobutton2.select();
    }

    public boolean getRadiobutton1Value() {
        return radiobutton1.getValue();
    }

    public boolean getRadiobutton2Value() {
        return radiobutton2.getValue();
    }

    public void pickColor(String color) {
        colorPicker.pickColor(color);
    }

    public String getPickedColor() {
        return colorPicker.gwtPickedColor();
    }

    public void pickDate(String date) {
        datePicker.pickDate(date);
    }

    public String getPickedDate() {
        return datePicker.getPickedDate();
    }

    public void setRange(int range) {
        rangeSlider.setValue(range);
    }

    public int getRange() {
        return rangeSlider.getValue();
    }
 }
