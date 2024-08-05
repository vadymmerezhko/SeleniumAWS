package org.example.data;

public class TargetPageOutput extends SmartDataObject {
    private SmartType header;
    private SmartType status;

    public String getHeader() {
        return header.toString();
    }

    public TargetPageOutput setHeader(String header) {
        this.header.setValue(header);
        return this;
    }

    public TargetPageOutput initialize() {
        header = new SmartType(this);
        status = new SmartType(this);
        return this;
    }

    public String getStatus() {
        return status.toString();
    }

    public TargetPageOutput setStatus(String status) {
        this.status.setValue(status);
        return this;
    }
}
