package org.example.data;

import lombok.Getter;

public class TargetPageOutput extends SmartDataObject {
    @Getter
    private final SmartType header = new SmartType();
    @Getter
    private final SmartType status = new SmartType();

    public TargetPageOutput() {
        super();
        initialize();
    }
}
