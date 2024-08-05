package org.example.data;

public class TargetPageOutput extends SmartDataObject {
    private final SmartType header = SmartType.auto();
    private final SmartType status = SmartType.auto();

    public TargetPageOutput() {
        super();
        initialize();
    }

    public String getHeader() {
        return header.toString();
    }

    public TargetPageOutput setHeader(String header) {
        this.header.setString(header);
        return this;
    }

    public String getStatus() {
        return status.toString();
    }

    public TargetPageOutput setStatus(String status) {
        this.status.setString(status);
        return this;
    }
}
