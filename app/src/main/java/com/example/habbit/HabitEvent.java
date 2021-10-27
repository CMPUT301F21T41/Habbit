package com.example.habbit;

public class HabitEvent {
    private boolean completedOnTime;
    // TODO: something that holds an image attachment
    // TODO: something that holds geolocation
    private String comment;
    private String dateCompleted; // TODO: would be cool to know what date the habit event was completed

    public HabitEvent(boolean completedOnTime, String comment) {
        this.completedOnTime = completedOnTime;
        this.comment = comment;
    }

    public boolean isCompletedOnTime() {
        return completedOnTime;
    }

    public void setCompletedOnTime(boolean completedOnTime) {
        this.completedOnTime = completedOnTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
