package com.application.labsolutions.listviews;

public class ListUserDetails {
    int image;
    String name;
    String phoneNumber;
    String uid;
    String emailId;
    String type;

    public ListUserDetails(int image, String name, String phoneNumber, String uid, String emailId, String type) {
        this.image = image;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.emailId = emailId;
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getType() {
        return type;
    }
}
