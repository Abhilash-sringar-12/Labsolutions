package com.application.labsolutions.pojos;

import java.util.Map;

public class DateInfo {
    String date;
    String time;


    Map<String, String> timeStamp;

    public DateInfo(String date, String time, Map<String, String> timeStamp) {
        this.date = date;
        this.time = time;
        this.timeStamp = timeStamp;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }


    public Map<String, String> getTimeStamp() {
        return timeStamp;
    }

}
