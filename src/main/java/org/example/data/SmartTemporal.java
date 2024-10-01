package org.example.data;

import java.util.Date;

/**
 * Smart temporal interface.
 */
public interface SmartTemporal extends FormattedValue {

    Date toDate();
}
