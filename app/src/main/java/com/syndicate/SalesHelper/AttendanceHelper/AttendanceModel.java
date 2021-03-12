package com.syndicate.SalesHelper.AttendanceHelper;

import java.util.Date;

public class AttendanceModel implements Comparable<AttendanceModel>{

    String name;
    String date;
    String time;
    Date dateD;

    public AttendanceModel() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDateD() {
        return dateD;
    }

    public void setDateD(Date dateD) {
        this.dateD = dateD;
    }

    @Override
    public int compareTo(AttendanceModel o) {
        return this.getDateD().compareTo(o.dateD);
    }
}

