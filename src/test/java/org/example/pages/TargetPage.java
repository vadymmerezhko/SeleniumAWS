package org.example.pages;

import lombok.Getter;
import org.example.annotations.SmartElement;
import org.example.drivers.elements.Label;

@SmartElement
@Getter
public class TargetPage extends SmartPage {

    private Label header;
    private Label status;
}
