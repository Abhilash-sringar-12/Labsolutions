package com.example.labsolutions.listviews;

import java.util.Comparator;

public class ActivitiesInfoDetails {
    String activityId;
    String instrumentId;
    String activityName;
    String activityDescription;
    String activitystatus;
    long updateTime;

    public ActivitiesInfoDetails(String activityId, String instrumentId, String activityName, String activityDescription, String activitystatus, long timeStamp) {
        this.instrumentId = instrumentId;
        this.activityName = activityName;
        this.activityDescription = activityDescription;
        this.activitystatus = activitystatus;
        this.activityId = activityId;
        this.updateTime = timeStamp;
    }

    public static Comparator<ActivitiesInfoDetails> activites =
            new Comparator<ActivitiesInfoDetails>() {
                @Override
                public int compare(ActivitiesInfoDetails activitiesInfoDetails, ActivitiesInfoDetails t1) {
                    return Long.compare(activitiesInfoDetails.getUpdateTime(), t1.getUpdateTime());
                }
            };

    public String getActivityId() {
        return activityId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public String getActivitystatus() {
        return activitystatus;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

}
