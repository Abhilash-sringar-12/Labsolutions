package com.example.labsolutions.services;


public class NotificationSender {
    Data data;

    public NotificationSender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    String to;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
