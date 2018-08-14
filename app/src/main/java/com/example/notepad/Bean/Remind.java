package com.example.notepad.Bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class Remind extends DataSupport implements Serializable {

    private String userEmail;//用户
    private String schedule;//日程
    private String dateTime;

    public Remind() {
    }

    public Remind(String userEmail, String schedule, String dateTime) {
        this.userEmail = userEmail;
        this.schedule = schedule;
        this.dateTime = dateTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
