package org.example.data;

import lombok.Getter;

@SuppressWarnings("unused")
@org.example.annotations.SmartValue
@Getter
public class TargetPageOutput extends SmartData {
    private SmartValue header;
    private SmartValue status;
}
