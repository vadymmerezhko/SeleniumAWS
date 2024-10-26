package org.example.unit;

import org.example.annotations.RunAlone;
import org.example.data.SmartLocalDate;
import org.example.data.SmartValue;
import org.example.ui.elements.*;
import org.example.ui.elements.Button;
import org.example.ui.elements.Checkbox;
import org.example.ui.elements.Label;
import org.example.ui.wrappers.WebSynchronizer;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.pages.TestPage;
import org.example.testng.RetryAnalyzer;
import org.example.tests.BaseTest;
import org.example.utils.FileSystemUtils;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;


public class WebElementsTest extends BaseTest {
    private static final String WEB_PAGE_URL = "https://www.selenium.dev/selenium/web/web-form.html";
    private static final String WEB_ELEMENTS_TEST = "src/test/java/org/example/unit/WebElementsTest.java";
    private static final String MULTI_SELECT_PAGE_URL =
            "file:///" + FileSystemUtils.getCurrentFolderPath() +
            "/src/test/resources/html/MultiSelectPage.html";

    private static final String RANGE_SLIDER_PAGE_URL =
            "file:///" + FileSystemUtils.getCurrentFolderPath() +
            "/src/test/resources/html/RangeSliderPage.html";

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterTextWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), "Test value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @RunAlone // Run test when other tests wait to provide valid clipboard value
    public void testTextInputToCutAndPasteText() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");
        // Select all input text
        textInput.selectAll();
        // Cut all input text
        textInput.cut();

        // Verify input text is empty
        Assert.assertEquals(textInput.getValue(), "");

        // Paste input cut text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), "Test value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @RunAlone // Run test when other tests wait to provide valid clipboard value
    public void testTextInputToCopyAndPasteText() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");
        // Select all input text
        textInput.selectAll();
        // Cut all input text
        textInput.copy();
        // Clear the text input text
        textInput.clear();

        // Verify input text is empty
        Assert.assertEquals(textInput.getValue(), "");

        // Paste copied input text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), "Test value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @RunAlone // Run test when other tests wait to provide valid clipboard value
    public void testTextInputToCopyAndPasteSubstringText() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");
        // Select all input text
        textInput.selectSubstring(5, 10);
        // Cut selected input substring text
        textInput.copy();
        // Clear the text input text
        textInput.clear();

        // Verify input text is empty
        Assert.assertEquals(textInput.getValue(), "");

        // Paste copied input substring text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), "value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @RunAlone // Run test when other tests wait to provide valid clipboard value
    public void testTextInputToCopyAndPasteSubstringTextByNegativeIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");
        // Select all input text
        textInput.selectSubstring(-5, -1);
        // Cut selected input substring text
        textInput.copy();
        // Clear the text input text
        textInput.clear();

        // Verify input text is empty
        Assert.assertEquals(textInput.getValue(), "");

        // Paste copied input substring text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), "value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @RunAlone // Run test when other tests wait to provide valid clipboard value
    public void testTextInputToCopyAndPasteSubstringTextByPositiveAndNegativeIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");
        // Select all input text
        textInput.selectSubstring(5, -1);
        // Cut selected input substring text
        textInput.copy();
        // Clear the text input text
        textInput.clear();

        // Verify input text is empty
        Assert.assertEquals(textInput.getValue(), "");

        // Paste copied input substring text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), "value");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    @RunAlone // Run test when other tests wait to provide valid clipboard value
    public void testTextInputToCopyAndPasteSubstringTextWithInvalidIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");
        textInput.selectSubstring(6, 2);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    @RunAlone // Run test when other tests wait to provide valid clipboard value
    public void testTextInputToCopyAndPasteSubstringTextWithTooBigIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText("Test value");
        textInput.selectSubstring(6, 200);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterTextWithValidSmartValueString() {
        TestPage testPage = new TestPage();
        TextInput textInput = testPage.getTextInput();
        testPage.open(WEB_PAGE_URL);
        // Enter text using SingleLineTextInput's enterText method
        textInput.enterText(new SmartValue("Test value"));

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), "Test value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterTextWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        SmartValue smartValue = new SmartValue("Multiline\nTest value");
        textarea.enterText(smartValue);

        Assert.assertEquals(textarea.getValue(), "Multiline\nTest value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextareaEnterTextWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        String text = "Multiline\nTest value";
        textarea.enterText(text);

        Assert.assertEquals(textarea.getValue(), text);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterTextWithEmptyString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        String text = "";
        textarea.enterText("Some text");
        textarea.enterText(text);

        Assert.assertEquals(textarea.getValue(), text);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testEnterTextWithNullSmartValue() {
        TestPage testPage = new TestPage();
        Textarea textarea = testPage.getTextarea();
        textarea.enterText((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testEnterTextWithNullString() {
        TestPage testPage = new TestPage();
        Textarea textarea = testPage.getTextarea();
        textarea.enterText((String) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPasswordWithValidText() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Password password = testPage.getPassword();
        // Enter valid password
        String validPassword = "StrongPassword123!";
        password.enterText(validPassword);

        Assert.assertEquals(password.getWrappedElement().getAttribute("value"), validPassword);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPasswordWithValidSmartValueString() {
        TestPage testPage = new TestPage();
        Password password = testPage.getPassword();
        testPage.open(WEB_PAGE_URL);
        // Enter valid password
        SmartValue validPassword = new SmartValue("StrongPassword123!");
        password.enterText(validPassword);

        Assert.assertEquals(password.getWrappedElement().getAttribute("value"),
                validPassword.toString());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPasswordWithEmptyText() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Password password = testPage.getPassword();
        // Enter empty password
        String emptyPassword = "";
        password.enterText("Some_password");
        password.enterText(emptyPassword);

        Assert.assertEquals(password.getWrappedElement().getAttribute("value"), emptyPassword);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testPasswordWithNullText() {
        TestPage testPage = new TestPage();
        Password password = testPage.getPassword();
        // Passing null as text should throw an exception
        password.enterText((String) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testPasswordWithNullSmartValue() {
        TestPage testPage = new TestPage();
        Password password = testPage.getPassword();
        // Passing null as text should throw an exception
        password.enterText((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testButtonClick() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Button button = testPage.getButton();
        // Simulate clicking the button
        button.click();
        WebSynchronizer synchro = new WebSynchronizer();
        synchro.waitForElementInvisibility(button.getSmartBy().getBy(), 1000);

        // New page should be open without this button
        Assert.assertFalse(button.isPresent());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testButtonByStrictKeywordClick() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Button buttonByStrictKeyword = testPage.getButtonByStrictKeyword();
        // Simulate clicking the button
        buttonByStrictKeyword.click();
        WebSynchronizer wait = new WebSynchronizer();
        wait.waitForElementInvisibility(buttonByStrictKeyword.getSmartBy().getBy(), 1000);

        // New page should be open without this button
        Assert.assertFalse(buttonByStrictKeyword.isPresent());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testButtonIsEnabled() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Button button = testPage.getButton();
        // Assert that the button is enabled
        Assert.assertTrue(button.isEnabled());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testCheck() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();

        // Simulate unchecking the checkbox first if it's already checked
        if (checkbox.isSelected()) {
            checkbox.uncheck();
        }
        checkbox.check();

        Assert.assertTrue(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testUncheck() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();

        // Simulate checking the checkbox first if it's unchecked
        if (!checkbox.isSelected()) {
            checkbox.check();
        }
        checkbox.uncheck();

        Assert.assertFalse(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueTrue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        checkbox.setValue(true);

        Assert.assertTrue(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueFalse() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        checkbox.setValue(false);

        Assert.assertFalse(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueSmartValueTrue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        SmartValue smartValue = new SmartValue(true);
        checkbox.setValue(smartValue);

        Assert.assertTrue(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueSmartValueFalse() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        SmartValue smartValue = new SmartValue(false);
        checkbox.setValue(smartValue);

        Assert.assertFalse(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testIsCheckedTrue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        // Ensure the checkbox is checked
        checkbox.setValue(true);

        Assert.assertTrue(checkbox.getValue());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testIsCheckedFalse() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        // Ensure the checkbox is unchecked
        checkbox.setValue(false);

        Assert.assertFalse(checkbox.getValue());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testIsCheckedTrueSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        // Ensure the checkbox is checked
        checkbox.setValue(true);

        Assert.assertTrue(checkbox.getSmartValue().toBoolean());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testIsCheckedFalseSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        // Ensure the checkbox is checked
        checkbox.setValue(false);

        Assert.assertFalse(checkbox.getSmartValue().toBoolean());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickColorWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        SmartValue smartColor = new SmartValue("#0088ff");
        colorPicker.pickColor(smartColor);

        Assert.assertEquals(colorPicker.getValue(), "#0088ff");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickColorWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        String color = "#ff5733";
        colorPicker.pickColor(color);

        Assert.assertEquals(colorPicker.getValue(), color);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickColorSmartValueWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        String color = "#ff5733";
        colorPicker.pickColor(color);

        Assert.assertEquals(colorPicker.getSmartValue().toString(), color);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testPickColorWithNullSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        // Passing null SmartValue should throw an exception
        colorPicker.pickColor((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testPickColorWithInvalidStringFormat() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        // Passing an invalid color format string should throw an exception
        colorPicker.pickColor("invalidColor");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        SmartValue smartOption = new SmartValue("Chicago");
        dataList.selectOption(smartOption);

        Assert.assertEquals(dataList.getValue(), smartOption.toString());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionSmartValueWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        SmartValue smartOption = new SmartValue("Chicago");
        dataList.selectOption(smartOption);

        Assert.assertEquals(dataList.getSmartValue().toString(), smartOption.toString());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        String option = "New York";
        dataList.selectOption(option);

        Assert.assertEquals(dataList.getValue(), option);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByValidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        int index = 1;  // Assume the first option
        dataList.selectOptionByIndex(index);

        Assert.assertEquals(dataList.getValue(), "San Francisco");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByValidSmartIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        SmartValue index = new SmartValue(2);
        dataList.selectOptionByIndex(index);

        Assert.assertEquals(dataList.getValue(), "New York");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSelectOptionWithNullSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        dataList.selectOption((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSelectOptionWithNullString() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.selectOption((String) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionWithBlankString() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.selectOption("");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionByNullSmartIndex() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.selectOptionByIndex(new SmartValue(null));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionByInvalidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        dataList.selectOptionByIndex(999);  // Invalid index
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickDateWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        SmartValue date = new SmartValue("12/25/2024");
        datePicker.pickDate(date);

        Assert.assertEquals(datePicker.getValue(), "12/25/2024");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickDateWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        String date = "01/01/2024";
        datePicker.pickDate(date);

        Assert.assertEquals(datePicker.getValue(), date);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Simulate picking a date
        datePicker.pickDate("05/10/2024");

        Assert.assertEquals(datePicker.getValue(), "05/10/2024");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Simulate picking a date
        datePicker.pickDate("12/25/2024");
        SmartValue smartValue = datePicker.getSmartValue();

        Assert.assertEquals(smartValue.toString(), "12/25/2024");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetSmartLocalDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Simulate picking a date
        datePicker.pickDate("12/25/2024");
        SmartLocalDate smartLocalDate = datePicker.getSmartLocalDate();

        Assert.assertEquals(smartLocalDate.toString(), "12/25/2024");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        SmartValue option = new SmartValue("One");
        dropdown.selectOption(option);

        Assert.assertEquals(dropdown.getSelectedOption(), "One");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        String  option = "Three";
        dropdown.selectOption(option);

        Assert.assertEquals(dropdown.getSelectedOption(), "Three");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionByValidValueString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        String value = "2";
        dropdown.selectOptionByValue(value);

        Assert.assertEquals(dropdown.getSelectedOption(), "Two");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionByValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        SmartValue value = new SmartValue(1);
        dropdown.selectOptionByValue(value);

        Assert.assertEquals(dropdown.getSelectedOption(), "One");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionByValidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        int index = 2; // Assuming index 2 corresponds to "Three"
        dropdown.selectOptionByIndex(index);

        Assert.assertEquals(dropdown.getSelectedOption(), "Two");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownGetSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        // Simulate selecting an option
        dropdown.selectOptionByValue(new SmartValue(2));
        SmartValue smartValue = dropdown.getSmartValue();

        Assert.assertEquals(smartValue.toString(), "Two");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testDropdownSelectOptionWithBlankSmartValue() {
        TestPage testPage = new TestPage();
        Dropdown dropdown = testPage.getDropdown();
        SmartValue option = new SmartValue("");

        dropdown.selectOption(option);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testDropdownSelectOptionWithInvalidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        SmartValue option = new SmartValue("Invalid");

        dropdown.selectOption(option);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDropdownSelectOptionByValueWithBlankString() {
        TestPage testPage = new TestPage();
        Dropdown dropdown = testPage.getDropdown();
        dropdown.selectOptionByValue("");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testDropdownSelectOptionByValueWithInvalidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        dropdown.selectOptionByValue("Invalid");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDropdownSelectOptionByNullSmartValue() {
        TestPage testPage = new TestPage();
        Dropdown dropdown = testPage.getDropdown();
        dropdown.selectOptionByValue((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDropdownSelectOptionByNullSting() {
        TestPage testPage = new TestPage();
        Dropdown dropdown = testPage.getDropdown();
        dropdown.selectOptionByValue((String) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testDropdownSelectOptionByInvalidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        int invalidIndex = 999; // Assume there are fewer than 999 options

        dropdown.selectOptionByIndex(invalidIndex);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetSelectedOptionValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        // Simulate selecting an option
        dropdown.selectOptionByValue("2");
        String selectedValue = dropdown.getSelectedOptionValue();

        Assert.assertEquals(selectedValue, "2");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetSelectedOptionIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        // Simulate selecting an option by index
        dropdown.selectOptionByIndex(2); // Assuming index 2 corresponds to an option
        int selectedIndex = dropdown.getSelectedOptionIndex();

        Assert.assertEquals(selectedIndex, 2);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetSelectedOptionValueWithNoSelection() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        // Simulate a case where no option is selected
        String result = dropdown.getSelectedOptionValue();

        Assert.assertEquals(result, "Open this select menu");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetSelectedOptionIndexWithNoSelection() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        // Simulate a case where no option is selected
        int result = dropdown.getSelectedOptionIndex();

        Assert.assertEquals(result, 0);
    }

    @Test
    public void testSelectOptionByIndexWithValidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        SmartValue index = new SmartValue(2);
        dropdown.selectOptionByIndex(index);

        Assert.assertEquals(dropdown.getSelectedOptionIndex(), 2);
    }

    @Test
    public void testSelectOptionByIndexWithZeroIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        SmartValue index = new SmartValue(0);
        dropdown.selectOptionByIndex(index);

        Assert.assertEquals(dropdown.getSelectedOptionIndex(), 0);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionByIndexWithMaxIntegerIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        SmartValue index = new SmartValue(Integer.MAX_VALUE);
        dropdown.selectOptionByIndex(index);

        Assert.assertEquals(dropdown.getSelectedOptionIndex(), Integer.MAX_VALUE);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByIndexWithNullIndex() {
        TestPage testPage = new TestPage();
        Dropdown dropdown = testPage.getDropdown();
        SmartValue index = null;
        dropdown.selectOptionByIndex(index);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionByIndexWithNonIntegerSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        SmartValue index = new SmartValue("nonIntegerValue");
        dropdown.selectOptionByIndex(index);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testFieldConstructorWithValidSelector() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Field field = testPage.getField();
        Assert.assertNotNull(field);
        Assert.assertEquals(field.getTagName(), "h1");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetFieldValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Field field = testPage.getField();
        String expectedValue = "Web form";
        String actualValue = field.getText();

        Assert.assertEquals(actualValue, expectedValue);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterFilePathWithValidFilePath() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        FileInput fileInput = testPage.getFileInput();
        // Call the method with a valid file path
        String validFilePath = WEB_ELEMENTS_TEST;
        File expectedFile = new File(validFilePath);
        fileInput.enterFilePath(validFilePath);

        // Verify the entered file path using getValue
        Assert.assertEquals(new File(fileInput.getValue()), expectedFile);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterFilePathWithFileObject() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        FileInput fileInput = testPage.getFileInput();
        // Create a valid File object
        File file = new File(WEB_ELEMENTS_TEST);
        // Call the method with a File object
        fileInput.enterFilePath(file);

        // Verify the entered file path using getValue
        Assert.assertEquals(fileInput.getValue(), FileSystemUtils.normalizeFilePathString(file.getPath()));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterFilePathWithSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        FileInput fileInput = testPage.getFileInput();
        // Create a valid SmartValue object
        SmartValue fileSmartValue = new SmartValue(WEB_ELEMENTS_TEST);
        // Call the method with a SmartValue object
        fileInput.enterFilePath(fileSmartValue);

        // Verify the entered file path using getValue
        Assert.assertEquals(fileInput.getValue(), fileSmartValue.toString());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testFileInputGetSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        FileInput fileInput = testPage.getFileInput();
        String validFilePath = WEB_ELEMENTS_TEST;
        // Enter a valid file path
        fileInput.enterFilePath(validFilePath);

        // Verify the SmartValue returned by getSmartValue
        SmartValue smartValue = fileInput.getSmartValue();
        Assert.assertEquals(smartValue.toFile(), new File(validFilePath));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testFileInputGetValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        FileInput fileInput = testPage.getFileInput();
        String validFilePath = WEB_ELEMENTS_TEST;
        // Enter a valid file path
        fileInput.enterFilePath(validFilePath);

        // Verify the file path returned by getValue
        Assert.assertEquals(new File(fileInput.getValue()), new File(validFilePath));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testEnterFilePathWithInvalidFilePath() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        FileInput fileInput = testPage.getFileInput();
        // Provide an invalid file path
        String invalidFilePath = "invalidFilePath";
        // This should throw an exception due to invalid file path format
        fileInput.enterFilePath(invalidFilePath);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testEnterFilePathWithNullFile() {
        TestPage testPage = new TestPage();
        FileInput fileInput = testPage.getFileInput();
        // Call enterFilePath with null File object
        fileInput.enterFilePath((File) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testEnterFilePathWithNullSmartValue() {
        TestPage testPage = new TestPage();
        FileInput fileInput = testPage.getFileInput();
        // Call enterFilePath with null SmartValue
        fileInput.enterFilePath((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testEnterFilePathWithNullPath() {
        TestPage testPage = new TestPage();
        FileInput fileInput = testPage.getFileInput();
        // Call enterFilePath with null Path object
        fileInput.enterFilePath((Path) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetValueWithInvalidAttribute() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        FileInput fileInput = testPage.getFileInput();
        // Simulate a scenario where getElement().getAttribute("value") returns invalid data
        // This should throw a SmartRuntimeException
        String result = fileInput.getValue();

        Assert.assertEquals(result, "");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetElementValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Label label = testPage.getLabel();
        String expectedLabelText = "Password";

        Assert.assertEquals(label.getText(), expectedLabelText);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testGetElementWithInvalidSelector() {
        // Initialize the Label with a selector that does not exist on the page
        By invalidSelector = By.id("invalidLabelId");
        new Label(invalidSelector).getText();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByText() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid option by its text
        String optionText = "Option 1";
        multiselect.selectOption(optionText);

        // Verify that the correct option is selected
        Assert.assertEquals(multiselect.getFirstSelectedOptionText(), optionText);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid option by its value
        String optionValue = "1";
        multiselect.selectOptionByValue(optionValue);

        // Verify that the correct option value is selected
        Assert.assertEquals(multiselect.getFirstSelectedOptionValue(), optionValue);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.selectOptionByIndex(0);

        // Verify that the correct option index is selected
        Assert.assertEquals(multiselect.getFirstSelectedOptionText(), "Option 1");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDeselectOptionByText() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate deselecting an option by its text
        String optionText = "Option 1";
        multiselect.selectOption(optionText);
        multiselect.deselectOption(optionText);

        // Verify that no option is selected
        Assert.assertTrue(multiselect.getAlSelectedOptions().isEmpty());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetAllSelectedOptions() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting multiple options
        multiselect.selectOption("Option 1");
        multiselect.selectOption("Option 2");

        // Verify that all selected options are returned
        List<String> selectedOptions = multiselect.getAlSelectedOptions();
        Assert.assertTrue(selectedOptions.contains("Option 1"));
        Assert.assertTrue(selectedOptions.contains("Option 2"));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetAllSelectedValues() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting multiple options by value
        multiselect.selectOptionByValue("1");
        multiselect.selectOptionByValue("2");

        // Verify that all selected option values are returned
        List<String> selectedValues = multiselect.getAlSelectedValues();
        Assert.assertTrue(selectedValues.contains("1"));
        Assert.assertTrue(selectedValues.contains("2"));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByTextWithNullValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOption with a null value
        multiselect.selectOption((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByValueWithEmptyValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOptionByValue with an empty value
        multiselect.selectOptionByValue("");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testDeselectOptionWithInvalidText() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Deselect a non-existent option
        multiselect.deselectOption("Invalid Option");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByIndexWithNullValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOptionByIndex with a null SmartValue
        multiselect.selectOptionByIndex(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDeselectOptionByValueWithNull() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Call deselectOptionByValue with a null value
        multiselect.deselectOptionByValue((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testDeselectOptionByIndexWithInvalidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Deselect an option by an invalid index
        multiselect.deselectOptionByIndex(999);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDeselectOptionByIndexWithValidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Assume the multiselect has some options selected initially
        int validIndex = 1;
        multiselect.selectOptionByIndex(validIndex);
        // Deselect the option using a valid index
        multiselect.deselectOptionByIndex(new SmartValue(validIndex));

        // Assert that the option is no longer selected
        Assert.assertFalse(multiselect.getAlSelectedOptions().contains("Option 2"));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDeselectOptionByIndexWithZeroIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Select the option at index 0
        multiselect.selectOptionByIndex(0);
        // Deselect the option using index 0
        multiselect.deselectOptionByIndex(new SmartValue(0));

        // Assert that the option is no longer selected
        Assert.assertFalse(multiselect.getAlSelectedOptions().contains("Option 1"));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDeselectOptionByIndexWithNullIndex() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Call deselectOptionByIndex with a null SmartValue
        multiselect.deselectOptionByIndex(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDeselectOptionByIndexWithNegativeIndex() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Call deselectOptionByIndex with a negative index
        multiselect.deselectOptionByIndex(new SmartValue(-1));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testDeselectOptionByIndexWithNonExistentIndex() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Assume the multiselect has only a few options
        int nonExistentIndex = 100;

        // Attempt to deselect a non-existent index
        multiselect.deselectOptionByIndex(new SmartValue(nonExistentIndex));
    }

    @Test
    public void testDeselectOptionWithValidOption() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        SmartValue option = new SmartValue("Option 2");
        multiselect.selectOption(option);
        multiselect.deselectOption(option);

        Assert.assertTrue(multiselect.getAlSelectedOptions().isEmpty());
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testDeselectOptionWithSmartValueNullOption() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.deselectOption((SmartValue) null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testDeselectOptionWithStringNullOption() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.deselectOption((String) null);
    }

    @Test
    public void testSelectOptionByValueWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        SmartValue validValue = new SmartValue("2");
        multiselect.selectOptionByValue(validValue);

        Assert.assertTrue(multiselect.getAlSelectedValues().contains("2"));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByValueWithStringNullSmartValue() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.selectOptionByValue((String) null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByValueWithNullSmartValueSmartValue() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.selectOptionByValue((SmartValue) null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDeselectOptionWithBlankOption() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        SmartValue option = new SmartValue("");
        multiselect.deselectOption(option);
    }


    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectRadiobutton() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Radiobutton radiobutton = testPage.getRadiobutton();

        // Ensure the radiobutton is not selected before selecting it
        if (radiobutton.isSelected()) {
            radiobutton.click(); // Deselect if already selected
        }
        // Select the radiobutton
        radiobutton.select();

        // Assert that the radiobutton is now selected
        Assert.assertTrue(radiobutton.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetValueForSelectedRadiobutton() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Radiobutton radiobutton = testPage.getRadiobutton();
        // Select the radiobutton
        radiobutton.select();

        // Assert that getValue() returns true
        Assert.assertTrue(radiobutton.getValue());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSlider() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Set the value to 3
        rangeSlider1.setValue(3);

        // Assert that the value is correctly set to 3
        Assert.assertEquals(rangeSlider1.getValue(), 3);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueUsingSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Set the value using number SmartValue
        SmartValue smartValue = new SmartValue(8);
        rangeSlider1.setValue(smartValue);

        // Assert that the value is correctly set to 8
        Assert.assertEquals(rangeSlider1.getValue(), 8);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueUsingStringSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Set the value using string SmartValue
        SmartValue smartValue = new SmartValue("7");
        rangeSlider1.setValue(smartValue);

        // Assert that the value is correctly set to 7
        Assert.assertEquals(rangeSlider1.getValue(), 7);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSliderWithNegativeValues() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider2 = testPage.getRangeSlider2();
        // Set the value to -333.0
        rangeSlider2.setValue(-333.0);

        // Assert that the value is correctly set to -333.0
        Assert.assertEquals(rangeSlider2.getValue(), -333);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSliderWithDefaultValues() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider3 = testPage.getRangeSlider3();
        // Set the value to 0
        rangeSlider3.setValue(0);

        // Assert that the value is correctly set to 0
        Assert.assertEquals(rangeSlider3.getValue(), 0);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSliderWithValuesBelowOne() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider4 = testPage.getRangeSlider4();
        // Set the value to 0.9
        rangeSlider4.setValue(0.9);

        // Assert that the value is correctly set to 0.9
        Assert.assertEquals(rangeSlider4.getValue(), 0.9);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSetValueToRangeSliderWithTooBigValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider4 = testPage.getRangeSlider4();
        // Set the value to 1.0 that is bigger than max=0.9
        rangeSlider4.setValue(1.0);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSetValueToRangeSliderWithTooSmallValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider4 = testPage.getRangeSlider4();
        // Set the value to 0.09 that is smaller than min=0.1
        rangeSlider4.setValue(0.09);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testSetValueWithNullSmartValue() {
        TestPage testPage = new TestPage();
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Attempt to set a null SmartValue
        rangeSlider1.setValue((SmartValue) null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartRuntimeException.class)
    public void testSetValueWithInvalidTypeSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Attempt to set a invalid type SmartValue
        rangeSlider1.setValue(new SmartValue(true));
    }
}
