package org.example.data;

import lombok.Getter;

public class TargetPageOutput extends SmartDataObject {
    @Getter
    private final SmartType header = SmartType.auto();
    @Getter
    private final SmartType status = SmartType.auto();

    public TargetPageOutput() {
        super();
        initialize();
    }
}
