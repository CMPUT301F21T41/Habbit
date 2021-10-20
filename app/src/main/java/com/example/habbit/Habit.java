package com.example.habbit;

import java.util.Date;

public class Habit {
    private String title; // up to 20 characters
    private String reason; // up to 30 characters
    private String date;

    public Habit(String title, String reason, String date) {
        this.title = title;
        this.reason = reason;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
