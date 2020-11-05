package com.application.labsolutions.pojos;

public class ActivityInfo {

    String instrumentId;
    String callType;
    String modelAndMake;
    String problemDescription;

    public ActivityInfo(String instrumentId, String modelAndMake, String callType, String problemDescription) {
        this.instrumentId = instrumentId;
        this.callType = callType;
        this.modelAndMake = modelAndMake;
        this.problemDescription = problemDescription;
    }

    public String getModelAndMake() {
        return modelAndMake;
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
