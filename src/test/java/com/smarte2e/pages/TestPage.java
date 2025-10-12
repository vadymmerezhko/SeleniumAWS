package com.smarte2e.pages;

import lombok.Getter;
import com.smarte2e.ui.elements.Button;
import com.smarte2e.ui.elements.Checkbox;
import com.smarte2e.ui.elements.ColorInput;
import com.smarte2e.ui.elements.DataList;
import com.smarte2e.ui.elements.DateInput;
import com.smarte2e.ui.elements.Dropdown;
import com.smarte2e.ui.elements.EmailInput;
import com.smarte2e.ui.elements.Field;
import com.smarte2e.ui.elements.FileInput;
import com.smarte2e.ui.elements.HiddenInput;
import com.smarte2e.ui.elements.Label;
import com.smarte2e.ui.elements.Multiselect;
import com.smarte2e.ui.elements.NumberInput;
import com.smarte2e.ui.elements.PasswordInput;
import com.smarte2e.ui.elements.Radiobutton;
import com.smarte2e.ui.elements.RangeSlider;
import com.smarte2e.ui.elements.SearchInput;
import com.smarte2e.ui.elements.TelephoneInput;
import com.smarte2e.ui.elements.TextInput;
import com.smarte2e.ui.elements.Textarea;
import com.smarte2e.ui.elements.TimeInput;
import com.smarte2e.ui.elements.URLInput;
import com.smarte2e.ui.selectors.SmartBy;
import com.smarte2e.ui.pages.SmartPage;
import org.openqa.selenium.By;

@Getter
public class TestPage extends SmartPage {
    private final TextInput textInput = new TextInput(SmartBy.id("my-text-id"));
    private final PasswordInput passwordInput = new PasswordInput(By.name("my-password"));
    private final Textarea textarea = new Textarea(SmartBy.name("my-textarea"));
    private final Button button = new Button(SmartBy.keyword("Submit"));
    private final Button buttonByStrictKeyword = new Button(SmartBy.strictKeyword("Submit"));
    private final Checkbox checkbox = new Checkbox(By.id("my-check-2"));
    private final ColorInput colorInput = new ColorInput(By.name("my-colors"));
    private final DataList dataList = new DataList(By.name("my-datalist"));
    private final DateInput dateInput = new DateInput(By.name("my-date"));
    private final Dropdown dropdown = new Dropdown(By.name("my-select"));
    private final Field field = new Field(By.tagName("h1"));
    private final FileInput fileInput = new FileInput(SmartBy.name("my-file"));
    private final Label label = new Label(SmartBy.keyword("Password"));
    private final Multiselect multiselect = new Multiselect(SmartBy.id("multiselect"));
    private final Radiobutton radiobutton = new Radiobutton(SmartBy.id("my-radio-2"));
    private final RangeSlider rangeSlider1 = new RangeSlider(SmartBy.name("my-range"));
    private final RangeSlider rangeSlider2 = new RangeSlider(SmartBy.id("range2"));
    private final RangeSlider rangeSlider3 = new RangeSlider(SmartBy.id("range3"));
    private final RangeSlider rangeSlider4 = new RangeSlider(SmartBy.id("range4"));
    private final EmailInput emailInput = new EmailInput(SmartBy.id("email"));
    private final TelephoneInput telephoneInput = new TelephoneInput(SmartBy.id("phone"));
    private final URLInput urlInput = new URLInput(SmartBy.id("url"));
    private final SearchInput searchInput = new SearchInput(SmartBy.id("search"));
    private final NumberInput numberInput = new NumberInput(SmartBy.id("quantity"));
    private final HiddenInput hiddenInput = new HiddenInput(SmartBy.name("userId"));
    private final TimeInput timeInput = new TimeInput(SmartBy.id("meeting-time"));
}
