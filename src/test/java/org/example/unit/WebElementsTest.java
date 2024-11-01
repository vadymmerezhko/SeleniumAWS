package org.example.unit;

import org.example.annotations.RunAlone;
import org.example.data.SmartDate;
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
import org.example.utils.ConvertUtils;
import org.example.utils.FileSystemUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
        Assert.assertEquals(textInput.getValueString(), "Test value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextInputSetValueWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.setValue("Test value");

        // Verify the input value
        Assert.assertEquals(textInput.getValue().toString(), "Test value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextInputSetValueWithValidInteger() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        // Enter text using SingleLineTextInput's enterText method
        textInput.setValue(123);

        // Verify the input value
        Assert.assertEquals(textInput.getValueString(), "123");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextInputSetSmartValueWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        TextInput textInput = testPage.getTextInput();
        SmartValue smartValue = new SmartValue("Some value");
        // Enter text using SingleLineTextInput's enterText method
        textInput.setValue(smartValue);

        // Verify the input value
        Assert.assertEquals(textInput.getValue(), smartValue);
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
        Assert.assertEquals(textInput.getValueString(), "");

        // Paste input cut text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValueString(), "Test value");
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
        Assert.assertEquals(textInput.getValueString(), "");

        // Paste copied input text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValueString(), "Test value");
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
        Assert.assertEquals(textInput.getValueString(), "");

        // Paste copied input substring text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValueString(), "value");
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
        Assert.assertEquals(textInput.getValueString(), "");

        // Paste copied input substring text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValueString(), "value");
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
        Assert.assertEquals(textInput.getValueString(), "");

        // Paste copied input substring text back
        textInput.paste();

        // Verify the input value
        Assert.assertEquals(textInput.getValueString(), "value");
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
        Assert.assertEquals(textInput.getValueString(), "Test value");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterTextWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        SmartValue smartValue = new SmartValue("Multiline\nTest value");
        textarea.enterText(smartValue);

        Assert.assertEquals(textarea.getValue(), smartValue);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextareaSetValueWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        SmartValue smartValue = new SmartValue("Multiline\nTest value");
        textarea.setValue(smartValue);

        Assert.assertEquals(textarea.getValue(), smartValue);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextareaSetValueWithValidValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        String value = "Multiline\nTest value";
        textarea.setValue(value);

        Assert.assertEquals(textarea.getValue().toString(), value);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextareaSetValueWithValidJsonValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("one", 1);
        jsonObject.put("two", 2);
        textarea.setValue(jsonObject);

        Assert.assertEquals(textarea.getValueString(), ConvertUtils.jsonObjectToString(jsonObject));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testTextareaEnterTextWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        String text = "Multiline\nTest value";
        textarea.enterText(text);

        Assert.assertEquals(textarea.getValueString(), text);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testEnterTextWithEmptyString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Textarea textarea = testPage.getTextarea();
        String text = "";
        textarea.enterText("Some text");
        textarea.enterText(text);

        Assert.assertEquals(textarea.getValueString(), text);
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
            checkbox.unselect();
        }
        checkbox.select();

        Assert.assertTrue(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testUncheck() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();

        // Simulate checking the checkbox first if it's unchecked
        if (!checkbox.isSelected()) {
            checkbox.select();
        }
        checkbox.unselect();

        Assert.assertFalse(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testCheckboxSetValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();

        // Simulate checking the checkbox first if it's unchecked
        if (!checkbox.isSelected()) {
            checkbox.setValue(true);
        }
        checkbox.setValue(false);

        Assert.assertFalse(checkbox.isSelected());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testCheckboxSetStringValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();

        // Simulate checking the checkbox first if it's unchecked
        if (!checkbox.isSelected()) {
            checkbox.setValue("true");
        }
        checkbox.setValue("false");

        Assert.assertFalse(checkbox.getValue().toBoolean());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testCheckboxSetSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();

        // Simulate checking the checkbox first if it's unchecked
        if (!checkbox.isSelected()) {
            checkbox.setValue(new SmartValue("true"));
        }
        checkbox.setValue(new SmartValue(false));

        Assert.assertFalse(checkbox.getValue().toBoolean());
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

        Assert.assertTrue(checkbox.getValue().toBoolean());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testIsCheckedFalse() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        // Ensure the checkbox is unchecked
        checkbox.setValue(false);

        Assert.assertFalse(checkbox.getValue().toBoolean());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testIsCheckedTrueSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        // Ensure the checkbox is checked
        checkbox.setValue(true);

        Assert.assertTrue(checkbox.getValue().toBoolean());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testIsCheckedFalseSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        // Ensure the checkbox is checked
        checkbox.setValue(false);

        Assert.assertFalse(checkbox.getValue().toBoolean());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCheckboxSetInvalidStringValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        checkbox.setValue("Invalid");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCheckboxSetNullValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Checkbox checkbox = testPage.getCheckbox();
        checkbox.setValue(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickColorWithValidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        SmartValue smartColor = new SmartValue("#0088ff");
        colorPicker.setValue(smartColor);

        Assert.assertEquals(colorPicker.getColor(), smartColor.toColor());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickColorWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        String colorString = "#ff5733";
        colorPicker.setValue(colorString);
        Color expectedColor = ConvertUtils.stringToColor(colorString);

        Assert.assertEquals(colorPicker.getValue().toColor(), expectedColor);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetSmartValueWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        String colorString = "#ff5733";
        SmartValue smartValue = new SmartValue(colorString);
        colorPicker.setValue(smartValue);
        Color expectedColor = smartValue.toColor();

        Assert.assertEquals(colorPicker.getColor(), expectedColor);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickColorSmartValueWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        String colorString = "#ff5733";
        colorPicker.setValue(colorString);
        SmartValue actualSmartValue = colorPicker.getValue();
        String actualColorSting = actualSmartValue.toString();

        Assert.assertEquals(actualColorSting, colorString);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetColorWithValidColorValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        Color color = Color.GREEN;
        colorPicker.setValue(color);

        Assert.assertEquals(colorPicker.getColor(), color);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testPickColorWithNullSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        // Passing null SmartValue should throw an exception
        colorPicker.setValue((SmartValue) null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPickColorWithInvalidStringFormat() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        // Passing an invalid color format string should throw an exception
        colorPicker.setValue("invalidColor");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testColorPickerSetValueWithNullValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        // Passing an invalid color format string should throw an exception
        colorPicker.setValue(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testColorPickerSetSmartValueWithInvalidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        // Passing null should throw an exception
        colorPicker.setValue(new SmartValue("invalidColor"));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testColorPickerSetSmartValueWithNullValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        ColorPicker colorPicker = testPage.getColorPicker();
        // Passing null string should throw an exception
        colorPicker.setValue(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        String option = "New York";
        dataList.setValue(option);

        Assert.assertEquals(dataList.getValue().toString(), option);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        String option = "New York";
        dataList.selectOption(option);

        Assert.assertEquals(dataList.getValueString(), option);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionWithValidIndexValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        SmartValue smartOption = new SmartValue("San Francisco");
        dataList.setValue(1);

        Assert.assertEquals(dataList.getValue(), smartOption);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionWithValidStringIndexValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        SmartValue smartOption = new SmartValue("San Francisco");
        dataList.setValue("1");

        Assert.assertEquals(dataList.getValue(), smartOption);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSetValueWithEmptyValue() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.setValue("");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionWithEmptyValue() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.selectOption("");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStValueWithNullValue() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.setValue(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionWithNullValue() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.selectOption(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByValidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        int index = 1;  // Assume the first option
        dataList.selectOptionByIndex(index);

        Assert.assertEquals(dataList.getValueString(), "San Francisco");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByValidSmartIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DataList dataList = testPage.getDataList();
        SmartValue index = new SmartValue(2);
        dataList.selectOptionByIndex(index);

        Assert.assertEquals(dataList.getValue().toString(), "New York");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSelectOptionWithNullString() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.setValue((String) null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionWithBlankString() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.setValue("");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSelectOptionByNullSmartIndex() {
        TestPage testPage = new TestPage();
        DataList dataList = testPage.getDataList();
        dataList.selectOptionByIndex(new SmartValue(null));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
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
        SmartValue smartValue = new SmartValue("12/25/2024");
        datePicker.setSmartValue(smartValue);

        Assert.assertEquals(
                datePicker.getValue().toLocalDate(),
                smartValue.toLocalDate());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testPickDateWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        String dateString = "01/01/2024";
        datePicker.setValue(dateString);

        Assert.assertEquals(datePicker.getValue().toString(), dateString);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDatePickerGetValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Simulate picking a date
        datePicker.setValue("05/10/2024");
        LocalDate expectedLocalDate = LocalDate.parse("2024-05-10");

        Assert.assertEquals(datePicker.getValue().toLocalDate(), expectedLocalDate);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDatePickerGetSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        String dateString = "05/10/2024";
        // Simulate picking a date
        datePicker.setValue(dateString);
        SmartValue expectedSmartValue = new SmartValue(SmartLocalDate.fromString(dateString));

        Assert.assertEquals(datePicker.getValue(), expectedSmartValue);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetDatePickerLocalDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Simulate picking a date
        datePicker.setValue("05/10/2024");
        SmartLocalDate expectedSmartLocalDate = SmartLocalDate.fromString("2024-05-10");

        Assert.assertEquals(datePicker.getSmartLocalDate(), expectedSmartLocalDate);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Simulate picking a date
        datePicker.setValue("12/25/2024");
        LocalDate expectedLocalDate = LocalDate.of(2024, 12, 25);
        SmartValue smartValue = datePicker.getValue();

        Assert.assertEquals(smartValue.toLocalDate(), expectedLocalDate);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetDatePickerSmartLocalDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Simulate picking a date
        datePicker.setValue("12/25/2024");
        SmartLocalDate smartLocalDate = datePicker.getSmartLocalDate();

        Assert.assertEquals(smartLocalDate, SmartLocalDate.fromString("12/25/2024"));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDatePickerSetValueWithLocalDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        SmartLocalDate smartLocalDate = SmartLocalDate.fromString("12/25/2024");
        // Simulate picking a date
        datePicker.setValue(smartLocalDate);

        Assert.assertEquals(datePicker.getValue().toSmartLocalDate(), smartLocalDate);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDatePickerSetValueWithSmartLocalDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        SmartLocalDate smartLocalDate = SmartLocalDate.fromString("12/25/2024");
        // Simulate picking a date
        datePicker.setValue(smartLocalDate);

        Assert.assertEquals(datePicker.getValue().toSmartLocalDate(), smartLocalDate);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDatePickerSetValueWithDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        Date date = ConvertUtils.stringToSmartDate("12/25/2024").toDate();
        // Simulate picking a date
        datePicker.setValue(date);

        Assert.assertEquals(datePicker.getValue().toDate(), date);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDatePickerSetValueWithSmartDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        SmartDate smartDate = ConvertUtils.stringToSmartDate("12/25/2024");
        // Simulate picking a date
        datePicker.setValue(smartDate);

        Assert.assertEquals(datePicker.getValue().toSmartDate(), smartDate);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDatePickerSetSmartValueWithLocalDate() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        SmartLocalDate smartLocalDate = SmartLocalDate.fromString("12/25/2024");
        SmartValue smartValue = new SmartValue(smartLocalDate);
        // Simulate picking a date
        datePicker.setSmartValue(smartValue);

        Assert.assertEquals(datePicker.getValue(), smartValue);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDatePickerSetValueWithInvalidDateString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Should throw an exception
        datePicker.setValue("Invalid date");
    }


    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDatePickerSetValueWithInvalidValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Should throw an exception
        datePicker.setValue(this);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDatePickerSetSmartValueWithInvalidSmartValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        DatePicker datePicker = testPage.getDatePicker();
        // Should throw an exception
        datePicker.setSmartValue(new SmartValue(this));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testDatePickerSetValueWithNullValue() {
        TestPage testPage = new TestPage();
        DatePicker datePicker = testPage.getDatePicker();
        // Should throw an exception
        datePicker.setValue(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testDatePickerSetSmartValueWithNullValue() {
        TestPage testPage = new TestPage();
        DatePicker datePicker = testPage.getDatePicker();
        // Should throw an exception
        datePicker.setSmartValue(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionWithValidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        String  option = "Three";
        dropdown.selectOption(option);

        Assert.assertEquals(dropdown.getValueString(), "Three");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionByValidValueString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        String value = "2";
        dropdown.selectOptionByValue(value);

        Assert.assertEquals(dropdown.getValueString(), "Two");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDropdownSelectOptionByValidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        int index = 2; // Assuming index 2 corresponds to "Three"
        dropdown.selectOptionByIndex(index);

        Assert.assertEquals(dropdown.getValueString(), "Two");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDropdownSelectOptionByValueWithBlankString() {
        TestPage testPage = new TestPage();
        Dropdown dropdown = testPage.getDropdown();
        dropdown.selectOptionByValue("");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDropdownSelectOptionByValueWithInvalidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Dropdown dropdown = testPage.getDropdown();
        dropdown.selectOptionByValue("Invalid");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testDropdownSelectOptionByNullSting() {
        TestPage testPage = new TestPage();
        Dropdown dropdown = testPage.getDropdown();
        dropdown.selectOptionByValue(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
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
        Assert.assertEquals(new File(fileInput.getValueString()), expectedFile);
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
        Assert.assertEquals(fileInput.getValue().toFile(), file);
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
        Assert.assertEquals(fileInput.getValue(), fileSmartValue);
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
        SmartValue smartValue = fileInput.getValue();
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
        Assert.assertEquals(fileInput.getValue().toFile(), new File(validFilePath));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
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
        String result = fileInput.getValue().toString();

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

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetElementWithInvalidSelector() {
        // Initialize the Label with a selector that does not exist on the page
        By invalidSelector = By.id("invalidLabelId");
        new Label(invalidSelector).getText();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByText() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid option by its text
        String optionText = "Option 1";
        multiselect.selectOption(optionText);

        // Verify that the correct option is selected
        Assert.assertEquals(multiselect.getFirstSelectedOption(), optionText);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionsByText() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid options by their text
        String[] options = {"Option 1", "Option 2"};
        List<String> optionsList = Arrays.asList(options);
        multiselect.selectOptions(optionsList);

        // Verify that the correct options are selected
        Assert.assertEquals(multiselect.getAllSelectedOptions(), optionsList);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionsByValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid options by their text
        String[] values = {"1", "2"};
        List<String> valuesList = Arrays.asList(values);
        multiselect.selectOptionsByValue(valuesList);

        // Verify that the correct option values are selected
        Assert.assertEquals(multiselect.getAllSelectedOptionValues(), valuesList);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionsByIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid options by their text
        Integer[] indexes = {0, 1};
        List<Integer> indexesList = Arrays.asList(indexes);
        multiselect.selectOptionsByIndexes(indexesList);

        // Verify that the correct option indexes are selected
        Assert.assertEquals(multiselect.getAllSelectedOptionIndexes(), indexesList);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDeselectOptionsByIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid options by their text
        Integer[] indexes = {0, 1};
        List<Integer> indexesList = Arrays.asList(indexes);
        multiselect.selectOptionsByIndexes(indexesList);
        multiselect.deselectOptionsByIndexes(indexesList);

        // Verify that the correct option indexes are selected
        Assert.assertTrue(multiselect.getAllSelectedOptionIndexes().isEmpty());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testMultiselectSelectOptionByValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        String value = "1";
        // Select option by value
        multiselect.selectOptionByValue(value);

        // Verify that the correct option is selected by value
        Assert.assertEquals(multiselect.getFirstSelectedOptionValue(), value);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testMultiselectDeselectOptionByValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        String value = "1";
        // Select option by value
        multiselect.selectOptionByValue(value);
        // Deselect selected option by value
        multiselect.deselectOptionByValue(value);

        // Verify that the correct option is deselected by value
        Assert.assertEquals(multiselect.getAllSelectedOptions().size(), 0);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDeselectOptionsByText() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid option by its text
        multiselect.selectOption("Option 3");
        String[] options = {"Option 1", "Option 2"};
        List<String> optionsList = Arrays.asList(options);
        List<String> expectedOptionsList = new ArrayList<>();
        expectedOptionsList.add("Option 3");
        // Simulate selecting a valid options by their text
        multiselect.selectOptions(optionsList);
        // Simulate deselecting a valid options by their text
        multiselect.deselectOptions(optionsList);

        // Verify that the correct options are deselected
        Assert.assertEquals(multiselect.getAllSelectedOptions(), expectedOptionsList);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testMultiselectDeselectOptionsByIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting a valid option by its index
        multiselect.selectOptionByIndex(3);
        Integer[] indexes = {0,  2};
        List<Integer> indexesList = Arrays.asList(indexes);
        List<Integer> expectedOptionsList = new ArrayList<>();
        expectedOptionsList.add(3);
        // Simulate selecting a valid options by their indexes
        multiselect.selectOptionsByIndexes(indexesList);
        // Simulate deselecting a valid options by their indexes
        multiselect.deselectOptionsByIndexes(indexesList);

        // Verify that the correct option indexes are deselected
        Assert.assertEquals(multiselect.getAllSelectedOptionIndexes(), expectedOptionsList);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testMultiselectSelectAllOptions() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        List<String> expectedOptionsList = new ArrayList<>();
        expectedOptionsList.add("Option 1");
        expectedOptionsList.add("Option 2");
        expectedOptionsList.add("Option 3");
        expectedOptionsList.add("Option 4");
        // Simulate selecting all options
        multiselect.selectAllOptions();

        // Verify that the all option are selected
        Assert.assertEquals(multiselect.getAllSelectedOptions(), expectedOptionsList);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testMultiselectDeselectAllOptions() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting all options
        multiselect.selectAllOptions();
        // Simulate deselecting all options
        multiselect.deselectAllOptions();

        // Verify that the all option are deselected
        Assert.assertTrue(multiselect.getAllSelectedOptionIndexes().isEmpty());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSelectOptionByValue() {
        TestPage testPage = new TestPage();
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
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.selectOptionByIndex(0);

        // Verify that the correct option index is selected
        Assert.assertEquals(multiselect.getFirstSelectedOption(), "Option 1");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDeselectOptionByText() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate deselecting an option by its text
        String optionText = "Option 1";
        multiselect.selectOption(optionText);
        multiselect.deselectOption(optionText);

        // Verify that no option is selected
        Assert.assertTrue(multiselect.getAllSelectedOptions().isEmpty());
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetAllSelectedOptions() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting multiple options
        multiselect.selectOption("Option 1");
        multiselect.selectOption("Option 2");

        // Verify that all selected options are returned
        List<String> selectedOptions = multiselect.getAllSelectedOptions();
        Assert.assertTrue(selectedOptions.contains("Option 1"));
        Assert.assertTrue(selectedOptions.contains("Option 2"));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetAllSelectedValues() {
        TestPage testPage = new TestPage();
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


    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testGetAllSelectedIndexes() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Simulate selecting multiple options by value
        multiselect.selectOptionByIndex(1);
        multiselect.selectOptionByIndex(2);

        // Verify that all selected option indexes are returned
        List<Integer> selectedValues = multiselect.getAllSelectedOptionIndexes();
        Assert.assertTrue(selectedValues.contains(1));
        Assert.assertTrue(selectedValues.contains(2));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionsByTextWithNullValue() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOption by null text
        multiselect.selectOptions(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionsByValuesWithNullValue() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOption by null text
        multiselect.selectOptionsByValue(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionsByValueWithBlankValue() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOption by null value
        multiselect.selectOptionByValue("  ");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionsByValueWithNullValue() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOption by null value
        multiselect.selectOptionByValue(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionsByValueWithNullValues() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        // Call deselectOption by null values
        multiselect.deselectOptionsByValues(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionsByIndexesWithNullIndexes() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        // Call deselectOption by null indexes
        multiselect.deselectOptionsByIndexes(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionsByValuesWithNullIndexes() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        // Call deselectOption by null values
        multiselect.deselectOptionsByValues(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByValueWithEmptyValue() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Call selectOptionByValue with an empty value
        multiselect.selectOptionByValue("");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
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

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDeselectOptionByIndexWithInvalidIndex() {
        TestPage testPage = new TestPage();
        testPage.open(MULTI_SELECT_PAGE_URL);
        Multiselect multiselect = testPage.getMultiselect();
        // Deselect an option by an invalid index
        multiselect.deselectOptionByIndex(999);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testDeselectOptionWithStringNullOption() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.deselectOption(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSelectOptionByValueWithStringNullSmartValue() {
        TestPage testPage = new TestPage();
        Multiselect multiselect = testPage.getMultiselect();
        multiselect.selectOptionByValue(null);
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
        Assert.assertTrue(radiobutton.isSelected());
    }

    @Test
    public void testRadiobuttonSetValueWithBooleanValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Radiobutton radiobutton = testPage.getRadiobutton();
        // Select the radiobutton
        radiobutton.setValue(true);

        // Assert that getValue() returns true
        Assert.assertTrue(radiobutton.isSelected());
    }

    @Test
    public void testRadiobuttonSetValueWithString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Radiobutton radiobutton = testPage.getRadiobutton();
        // Select the radiobutton
        radiobutton.setValue("true");

        // Assert that isSelected() returns true
        Assert.assertTrue(radiobutton.isSelected());
    }

    @Test
    public void testRadiobuttonSetValueWithFalseValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Radiobutton radiobutton = testPage.getRadiobutton();
        // Select the radiobutton
        radiobutton.setValue(false);

        // Assert that isSelected() returns false
        Assert.assertFalse(radiobutton.isSelected());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testRadiobuttonSetValueWithInvalidString() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Radiobutton radiobutton = testPage.getRadiobutton();
        // Select the radiobutton
        radiobutton.setValue("Invalid");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testRadiobuttonSetValueWithNullValue() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        Radiobutton radiobutton = testPage.getRadiobutton();
        // Select the radiobutton
        radiobutton.setValue(null);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSlider() {
        TestPage testPage = new TestPage();
        testPage.open(WEB_PAGE_URL);
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Set the value to 3
        rangeSlider1.setValue(3);

        // Assert that the value is correctly set to 3
        Assert.assertEquals(rangeSlider1.getRange(), 3);
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
        Assert.assertEquals(rangeSlider1.getRange(), 8);
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
        Assert.assertEquals(rangeSlider1.getRange(), 7);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSliderWithNegativeValues() {
        TestPage testPage = new TestPage();
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider2 = testPage.getRangeSlider2();
        // Set the value to -333.0
        rangeSlider2.setValue(-333.0);

        // Assert that the value is correctly set to -333.0
        Assert.assertEquals(rangeSlider2.getRange(), -333);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSliderWithDefaultValues() {
        TestPage testPage = new TestPage();
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider3 = testPage.getRangeSlider3();
        // Set the value to 0
        rangeSlider3.setValue(0);

        // Assert that the value is correctly set to 0
        Assert.assertEquals(rangeSlider3.getRange(), 0);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testSetValueToRangeSliderWithValuesBelowOne() {
        TestPage testPage = new TestPage();
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider4 = testPage.getRangeSlider4();
        // Set the value to 0.9
        rangeSlider4.setValue(0.9);

        // Assert that the value is correctly set to 0.9
        Assert.assertEquals(rangeSlider4.getValue().toDouble(), 0.9);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSetValueToRangeSliderWithTooBigValue() {
        TestPage testPage = new TestPage();
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider4 = testPage.getRangeSlider4();
        // Set the value to 1.0 that is bigger than max=0.9
        rangeSlider4.setValue(1.0);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSetValueToRangeSliderWithTooSmallValue() {
        TestPage testPage = new TestPage();
        testPage.open(RANGE_SLIDER_PAGE_URL);
        RangeSlider rangeSlider4 = testPage.getRangeSlider4();
        // Set the value to 0.09 that is smaller than min=0.1
        rangeSlider4.setValue(0.09);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSetValueWithNullSmartValue() {
        TestPage testPage = new TestPage();
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Attempt to set a null SmartValue
        rangeSlider1.setValue(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSetValueWithInvalidTypeSmartValue() {
        TestPage testPage = new TestPage();
        RangeSlider rangeSlider1 = testPage.getRangeSlider1();
        // Attempt to set a invalid type SmartValue
        rangeSlider1.setValue(new SmartValue(true));
    }
}
