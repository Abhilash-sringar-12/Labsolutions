package com.example.labsolutions.pojos;

public class ActivityInfo {

    String instrumentId;
    String callType;
    String problemDescription;

    public ActivityInfo(String instrumentId, String callType, String problemDescription) {
        this.instrumentId = instrumentId;
        this.callType = callType;
        this.problemDescription = problemDescription;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public String getCallType() {
        return callType;
    }

    public String getProblemDescription() {
        return problemDescription;
    }
}
