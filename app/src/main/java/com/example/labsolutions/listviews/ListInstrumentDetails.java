package com.example.labsolutions.listviews;

public class ListInstrumentDetails {

    String companyName;
    String instrumentId;
    String instrumentType;
    String department;
    String amcFromDate;
    String amcToDate;


    public ListInstrumentDetails(String companyName, String instrumentId, String instrumentType, String department, String amcFromDate, String amcToDate) {
        this.companyName = companyName;
        this.instrumentId = instrumentId;
        this.instrumentType = instrumentType;
        this.department = department;
        this.amcFromDate = amcFromDate;
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

    public String getAmcFromDate() {
        return amcFromDate;
    }

    public String getAmcToDate() {
        return amcToDate;
    }

}
