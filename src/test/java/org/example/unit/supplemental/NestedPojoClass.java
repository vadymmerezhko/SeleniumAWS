package org.example.unit.supplemental;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.Platform;

import java.time.LocalDate;

@Setter @Getter
public class NestedPojoClass {
    private Platform platform;
    private LocalDate date;
}
