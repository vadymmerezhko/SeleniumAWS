package org.example.pages;

import lombok.Getter;
import org.example.ui.elements.*;
import org.example.ui.pages.SmartPage;


/**
 * Web form class.
 */
@Getter
public class WebFormPage extends SmartPage {
    private final TextInput productName = new TextInput();
    private final PasswordInput password = new PasswordInput();
    private final Textarea description = new Textarea();
    private final Dropdown brand = new Dropdown();
    private final DataList model = new DataList();
    private final FileInput upload = new FileInput();
    private final Checkbox available = new Checkbox();
    private final Checkbox freeDelivery = new Checkbox();
    private final Radiobutton freeReturn = new Radiobutton();
    private final Radiobutton paidReturn = new Radiobutton();
    private final ColorInput color = new ColorInput();
    private final DateInput deliveryDate = new DateInput();
    private final RangeSlider weight = new RangeSlider();
    private final Button submit = new Button();
 }
