package com.application.labsolutions.listviews;

import java.util.Comparator;

public class LeaveDetails {
    String leaveType;
    String leaveFrom;
    String backToWork;
    String name;
    String leaveID;
    String totalLeaves;
    long timeStamp;


    public LeaveDetails(String leaveType, String leaveFrom, String backToWork, long timeStamp, String name, String leaveID, String totalLeaves) {
        this.leaveType = leaveType;
        this.leaveFrom = leaveFrom;
        this.backToWork = backToWork;
        this.timeStamp = timeStamp;
        this.name = name;
        this.leaveID = leaveID;
        this.totalLeaves = totalLeaves;
    }

    public static Comparator<LeaveDetails> leaves =
            new Comparator<LeaveDetails>() {
                @Override
                public int compare(LeaveDetails leaveDetails, LeaveDetails t1) {
                    return Long.compare(leaveDetails.getTimeStamp(), t1.getTimeStamp());
                }
            };

    public String getTotalLeaves() {
        return totalLeaves;
    }

    public void setTotalLeaves(String totalLeaves) {
        this.totalLeaves = totalLeaves;
    }

    public String getLeaveID() {
        return leaveID;
    }

    public void setLeaveID(String leaveID) {
        this.leaveID = leaveID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveFrom() {
        return leaveFrom;
    }

    public void setLeaveFrom(String leaveFrom) {
        this.leaveFrom = leaveFrom;
    }

    public String getBackToWork() {
        return backToWork;
    }

    public void setBackToWork(String backToWork) {
        this.backToWork = backToWork;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
