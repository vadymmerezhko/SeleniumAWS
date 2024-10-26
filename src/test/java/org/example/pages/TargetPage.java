package org.example.pages;

import lombok.Getter;
import org.example.ui.elements.Label;
import org.example.ui.pages.SmartPage;

@Getter
public class TargetPage extends SmartPage {

    private final Label header = new Label();
    private final Label status = new Label();
}
