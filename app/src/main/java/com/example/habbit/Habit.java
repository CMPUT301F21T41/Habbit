package com.example.habbit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Habit implements Serializable {
    /* attributes that define a Habit object */
    private String title; // up to 20 characters
    private String reason; // up to 30 characters
    private String date;
    private String id;

    /* to be implemented */
    // TODO: Should we use this arraylist, or just use Firestore to track habitEvents?
    private ArrayList<HabitEvent> habitEvents;
    private boolean checked = false; // TODO: this is temporary, habit behaviour is too complicated for to think about right now

    // empty constructor needed to use Firestore add()
    public Habit() {}

    public Habit(String title, String reason, String date) {
        this.title = title;
        this.reason = reason;
        this.date = date;
        habitEvents = new ArrayList<HabitEvent>();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<HabitEvent> getHabitEvents() {
        return habitEvents;
    }

    public void setHabitEvents(ArrayList<HabitEvent> habitEvents) {
        this.habitEvents = habitEvents;
    }

    public void addHabitEvent(HabitEvent habitEvent) {
        habitEvents.add(habitEvent);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
