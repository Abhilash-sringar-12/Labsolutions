package com.application.labsolutions.dateutils;

import java.text.DateFormat;
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
    public static long getTimeStamForLeaves(String leaveDate) {
        long timeStamp = 0l;
        try {
            SimpleDateFormat simpleDateFormat
                    = new SimpleDateFormat(
                    "dd-MM-yyyy");
            Date date = simpleDateFormat.parse(leaveDate);
            timeStamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static String formatDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(new Date(date));
    }

    public static double calculatedLeaves(String startDate, String backOn) {
        double numberOfDays = 0.0;
        try {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = df.parse(startDate);
            Date date2 = df.parse(backOn);
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);
            while (cal1.before(cal2)) {
                if ((Calendar.SATURDAY != cal1.get(Calendar.DAY_OF_WEEK)) && (Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK))) {
                    numberOfDays++;
                    cal1.add(Calendar.DATE, 1);
                } else {
                    cal1.add(Calendar.DATE, 1);
                }
            }
        } catch (
                ParseException e) {
            e.printStackTrace();
        }

        return numberOfDays;
    }
}
