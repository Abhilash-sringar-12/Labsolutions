package com.application.labsolutions.pojos;

public class InstrumentInfo {

    String companyName;
    String instrumentId;
    String instrumentType;
    String department;
    String amcFromDate;
    String amcToDate;


    public InstrumentInfo(String companyName, String instrumentId, String instrumentType, String department, String amcFromDate, String amcToDate) {
        this.companyName = companyName;
        this.instrumentId = instrumentId;
        this.instrumentType = instrumentType;
        this.department = department;
        this.amcFromDate =amcFromDate;
        this.amcToDate = amcToDate;
    }

    public String getAmcFromDate() {
        return amcFromDate;
    }

    public void setAmcFromDate(String amcFromDate) {
        this.amcFromDate = amcFromDate;
    }

    public String getAmcToDate() {
        return amcToDate;
    }

    public void setAmcToDate(String amcToDate) {
        this.amcToDate = amcToDate;
    }
    public String getCompanyName() {
        return companyName;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public String getDepartment() {
        return department;
    }
}
