package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.data.WebFormPageInput;
import org.example.data.WebFormPageOutput;
import org.example.exceptions.SmartRuntimeException;
import org.example.ui.elements.*;
import org.example.ui.pages.SmartPage;
import org.example.utils.DataValidator;

import java.time.LocalDate;


/**
 * Web form class.
 */
@Slf4j
@SuppressWarnings("unused")
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
    private final Button submitButton = new Button();

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
