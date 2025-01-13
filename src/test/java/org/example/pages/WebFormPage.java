package org.example.pages;

import lombok.Getter;
import org.example.ui.elements.*;
import org.example.ui.pages.SmartPage;


/**
 * Web form class.
 */
@Getter
public class WebFormPage extends SmartPage {
    private final TextInput textInput = new TextInput();
    private final PasswordInput passwordInput = new PasswordInput();
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
 }
