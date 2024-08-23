package org.example.pages;

import org.example.drivers.elements.Label;

public class TargetPage extends SmartPage {

    private final Label header = new Label();
    private final Label status = new Label();

    /**
     * TargetPage constructor that initializes web elements.
     */
    public TargetPage() {
        super();
        initialize();
    }

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
