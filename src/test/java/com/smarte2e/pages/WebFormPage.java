package com.smarte2e.pages;

import com.smarte2e.ui.selectors.SmartBy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.smarte2e.data.WebFormPageInput;
import com.smarte2e.data.WebFormPageOutput;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.ui.elements.Button;
import com.smarte2e.ui.elements.Checkbox;
import com.smarte2e.ui.elements.ColorInput;
import com.smarte2e.ui.elements.DataList;
import com.smarte2e.ui.elements.DateInput;
import com.smarte2e.ui.elements.Dropdown;
import com.smarte2e.ui.elements.PasswordInput;
import com.smarte2e.ui.elements.Radiobutton;
import com.smarte2e.ui.elements.RangeSlider;
import com.smarte2e.ui.elements.TextInput;
import com.smarte2e.ui.elements.Textarea;
import com.smarte2e.ui.pages.SmartPage;
import com.smarte2e.utils.DataValidator;

import java.time.LocalDate;


/**
 * Web form class.
 */
@Getter @Slf4j
public class WebFormPage extends SmartPage {
    private final TextInput productName = new TextInput();
    private final PasswordInput password = new PasswordInput();
    private final Textarea description = new Textarea();
    private final Dropdown brand = new Dropdown();
    private final DataList model = new DataList();
    // TODO: Uncomment when fixed
    //private final FileInput upload = new FileInput();
    private final Checkbox available = new Checkbox();
    private final Checkbox freeDelivery = new Checkbox();
    private final Radiobutton freeReturn = new Radiobutton();
    private final Radiobutton paidReturn = new Radiobutton();
    private final ColorInput color = new ColorInput();
    private final DateInput deliveryDate = new DateInput();
    private final RangeSlider weight = new RangeSlider();
    private final Button submitButton = new Button(SmartBy.image());

    public WebFormPageOutput fillWebForm(WebFormPageInput input) {
        DataValidator.notNull(input, "input");

        try {
            int thisYear = LocalDate.now().getYear();
            String descriptionKeyword = "my-textarea";

            open();
            log.info("Web Form page URL: {}", getCurrentUrl());

            password.enterText("Password123");
            description.setKeyword(descriptionKeyword);
            input.getDeliveryDate().setKeyword(thisYear);
            setAllInputs(input);
            log.debug("Web Form page is filled with input data: {}", input);

            WebFormPageOutput output = new WebFormPageOutput();
            output.getDeliveryDate().setKeyword(thisYear);
            setAllOutputs(output);

            log.debug("Web Form page output data is returned: {}", output);
            return output;
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Filling the Web Form failed.", e);
        }
    }

    public void submitWebForm() {
        try {
            submitButton.click();

            log.debug("Web Form page is submitted");
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Submitting the Web Form failed.", e);
        }
    }
 }
