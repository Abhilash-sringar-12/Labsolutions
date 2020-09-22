package com.example.labsolutions.dateutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtility {

    public void DateUtility() {

    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTimeStamp(String scheduledDate) {
        String timeStamp = "";
        try {
            SimpleDateFormat simpleDateFormat
                    = new SimpleDateFormat(
                    "dd MMM yyyy HH:mm:ss");
            Date date = simpleDateFormat.parse(scheduledDate);
            timeStamp = String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static long getTimeStampOfAmc(String scheduledDate) {
        long timeStamp = 0l;
        try {
            SimpleDateFormat simpleDateFormat
                    = new SimpleDateFormat(
                    "dd MMM yyyy");
            Date date = simpleDateFormat.parse(scheduledDate);
            timeStamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }
}
