package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.data.LoginPageInput;
import org.example.data.LoginPageOutput;
import org.example.exceptions.SmartRuntimeException;
import org.example.ui.elements.Button;
import org.example.ui.elements.PasswordInput;
import org.example.ui.elements.TextInput;
import org.example.ui.pages.SmartPage;
import org.example.utils.DataValidator;

@Slf4j
@SuppressWarnings("unused")
public class LoginPage extends SmartPage {
    private static final String PAGE_URL = "https://www.saucedemo.com/";

    private final TextInput userName = new TextInput();
    private final PasswordInput password = new PasswordInput();
    private final Button loginButton = new Button();

    /**
     * Fills login page form with input data.
     * @param input Login form input data.
     * @return Login form output data.
     */
    public LoginPageOutput fillLoginForm(LoginPageInput input) {
        DataValidator.notNull(input, "input");
        try {
            open(PAGE_URL);
            setAllInputs(input);
            log.debug("Login page is filled with input data: {}", input);

            LoginPageOutput output = new LoginPageOutput();
            setAllOutputs(output);
            log.debug("Login page output data is returned: {}", output);
            return output;
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Filling the Login page failed.", e);
        }
    }

    /**
     * Submits Login page form.
     */
    public void submitLoginForm() {
        try {
            loginButton.submit();
            log.debug("Login page is submitted");
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Submitting the Login form failed.", e);
        }
    }
}
