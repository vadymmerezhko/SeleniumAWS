package org.example.pages;

import lombok.Getter;
import org.example.ui.elements.*;
import org.example.ui.selectors.SmartBy;
import org.example.ui.pages.SmartPage;
import org.openqa.selenium.By;

@Getter
public class TestPage extends SmartPage {
    private final TextInput textInput = new TextInput(SmartBy.id("my-text-id"));
    private final Password password = new Password(By.name("my-password"));
    private final Textarea textarea = new Textarea(SmartBy.name("my-textarea"));
    private final Button button = new Button(SmartBy.keyword("Submit"));
    private final Button buttonByStrictKeyword = new Button(SmartBy.strictKeyword("Submit"));
    private final Checkbox checkbox = new Checkbox(By.id("my-check-2"));
    private final ColorPicker colorPicker = new ColorPicker(By.name("my-colors"));
    private final DataList dataList = new DataList(By.name("my-datalist"));
    private final DatePicker datePicker = new DatePicker(By.name("my-date"));
    private final Dropdown dropdown = new Dropdown(By.name("my-select"));
    private final Field field = new Field(By.tagName("h1"));
    private final FileInput fileInput = new FileInput(SmartBy.name("my-file"));
    private final Label label = new Label(SmartBy.keyword("Password"));
    private final Multiselect multiselect = new Multiselect(SmartBy.id("multiselect"));
    private final  Radiobutton radiobutton = new Radiobutton(SmartBy.id("my-radio-2"));
    private final RangeSlider rangeSlider1 = new RangeSlider(SmartBy.name("my-range"));
    private final RangeSlider rangeSlider2 = new RangeSlider(SmartBy.id("range2"));
    private final RangeSlider rangeSlider3 = new RangeSlider(SmartBy.id("range3"));
    private final RangeSlider rangeSlider4 = new RangeSlider(SmartBy.id("range4"));
}
