package com.smarte2e.unit.supplemental.classes;

import lombok.Getter;
import lombok.Setter;
import com.smarte2e.enums.Platform;

import java.time.LocalDate;

@Setter @Getter
public class NestedPojoClass {
    private Platform platform;
    private LocalDate date;
}
