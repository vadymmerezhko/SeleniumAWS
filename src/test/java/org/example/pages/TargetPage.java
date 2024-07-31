package org.example.pages;

import org.example.drivers.elements.Label;
import org.example.drivers.selectors.SmartBy;

public class TargetPage extends BasePage {

    private final Label header = new Label(this, SmartBy.magic());
    private final Label status = new Label(this, SmartBy.magic());

    /**
     * Returns Result page header text.
     * @return The text.
     */
    public String getHeaderText() {
        return header.getText();
    }

    /**
     * Returns Result page staus text.
     * @return The text.
     */
    public String getStatusText() {
        return status.getText();
    }
}
