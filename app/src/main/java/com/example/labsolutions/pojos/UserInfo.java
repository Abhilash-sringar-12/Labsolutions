package com.example.labsolutions.pojos;

public class UserInfo {
    String user;
    String mailId;
    String phoneNumber;
    String companyName;
    String department;
    String userType;
    String companyAddress;
    String uuid;


    public UserInfo(String user, String mailId, String phoneNumber, String companyName, String department, String userType, String companyAddress) {
        this.user = user;
        this.mailId = mailId;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.department = department;
        this.userType = userType;
        this.companyAddress = companyAddress;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getDepartment() {
        return department;
    }

    public String getUserType() {
        return userType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUuid() {
        return uuid;
    }
}
