package org.example.data;

import lombok.Getter;

public class TargetPageOutput extends SmartDataObject {
    @Getter
    private final SmartClass header = new SmartClass();
    @Getter
    private final SmartClass status = new SmartClass();

    public TargetPageOutput() {
        super();
        initialize();
    }
}
