package com.application.labsolutions.listviews;

public class ActivityInfo {
    String info;
    String time;
    String phone, mailId;


    public ActivityInfo(String info, String time, String phone, String mailId) {
        this.info = info;
        this.time = time;
        this.phone = phone;
        this.mailId = mailId;
    }

    public String getInfo() {
        return info;
    }

    public String getTime() {
        return time;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

}
