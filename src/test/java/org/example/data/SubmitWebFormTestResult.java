package org.example.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * The submitWebForm test result data record.
 * @param header The header text.
 * @param status The status text.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record SubmitWebFormTestResult(String header,
                                      String status) {
}
