package com.example.habbit.models;

import java.io.Serializable;

public class HabitEvent implements Serializable {
    // TODO: completedOnTime can't be done until we implement selecting days we want to do habits which is in our next milestone
    private boolean completedOnTime;
    // TODO: something that holds an image attachment
    // TODO: something that holds geolocation
    private String comment;
    private String id;
    private String dateCompleted; // TODO: would be cool to know what date the habit event was completed

    public HabitEvent(String comment) {
        this.comment = comment;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
