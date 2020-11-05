package com.application.labsolutions.services;

public class Data {
    String title;

    String message;

    String activityType;

    public Data() {

    }

    public Data(String title, String message, String activityType) {
        this.title = title;
        this.message = message;
        this.activityType = activityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

}
