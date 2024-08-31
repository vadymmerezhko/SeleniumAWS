package org.example.pages;

import lombok.Getter;
import org.example.annotations.SmartElement;
import org.example.drivers.elements.*;


/**
 * Web form class.
 */

@SmartElement
@Getter
public class WebFormPage extends SmartPage {

    private TextInput textInput;
    private Password password;
    private Textarea textarea;
    private Dropdown dropdown;
    private FileInput fileInput;
    private DataList dataList;
    private Checkbox checkbox1;
    private Checkbox checkbox2;
    private Radiobutton radiobutton1;
    private Radiobutton radiobutton2;
    private ColorPicker colorPicker;
    private DatePicker datePicker;
    private RangeSlider rangeSlider;
    private Button submitButton;
 }
