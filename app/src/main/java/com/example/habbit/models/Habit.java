package com.example.habbit.models;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Habit implements Serializable {
    /* attributes that define a Habit object */

    /**
     * This var is of type {@link String} and contains the title of a habit
     */
    private String title; // up to 20 characters

    /**
     * This var is of type {@link String} and contains the reason for starting a habit
     */
    private String reason; // up to 30 characters

    /**
     * This var is of type {@link String} and contains the date a habit was started
     */
    private String date;

    /**
     * This var is of type {@link String} and contains the unique id of the {@link Habit} object
     */
    private String id;

    /**
     * this var is of type {@link ArrayList<HabitEvent>} and contains a list of {@link HabitEvent} related to the habit
     */
    private ArrayList<HabitEvent> habitEvents;

    /**
     * This var is of type {@link Boolean} and represents whether or not the habit has been completed/checked in the list
     */
    private boolean checked = false;

    /**
     * This var is of type {@link java.util.HashMap} and represents the days of the week the habit is scheduled to hapeen
     */
    private HashMap<String, Boolean> schedule;

    // empty constructor needed to use Firestore add()
    public Habit() {}

    /**
     * Constructor for Habit class
     *
     * @param title give the title for a habit which should be of type {@link String}
     * @param reason give the reason for a habit which should be of type {@link String}
     * @param date give a date to be formatted and turned into {@link String}
     * @param schedule give a schedule for days of the week that the habit should be completed {@link HashMap}
     */
    public Habit(String title, String reason, String date, HashMap<String, Boolean> schedule) {
        this.title = title;
        this.reason = reason;
        this.date = date;
        habitEvents = new ArrayList<HabitEvent>(); // initialize habitEvents list
        this.schedule = schedule;
    }

    /**
     * This function returns {@link Habit#title}
     * @return The return type is {@link String}
     */
    public String getTitle() {
        return title;
    }

    /**
     * This function sets {@link Habit#title} which is of type {@link String}
     * @param title The title we wish to give to the habit
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This function returns {@link Habit#reason}
     * @return The return type is {@link String}
     */
    public String getReason() {
        return reason;
    }

    /**
     * This function sets {@link Habit#reason} which is of type {@link String}
     * @param reason the reason we want to give to the habit
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * This function returns {@link Habit#date} in Date Object form
     * @return The return type is {@link Date}
     */
    @SuppressLint("SimpleDateFormat")
    public Date getDateObject() {
        Date dateObj = null;
        try {
            dateObj = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateObj;
    }

    /**
     * This function returns {@link Habit#date}
     * @return The return type is {@link String}
     */
    public String getDate() {
        return date;
    }

    /**
     * This function sets {@link Habit#date} which is of type {@link String}
     * @param date The date we want to give to the habit
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This function returns {@link Habit#id}
     * @return The return type is {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * This function sets {@link Habit#id} which is of type {@link String}
     * @param id The id we wish to give to the habit
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This function returns {@link Habit#habitEvents}
     * @return The return type is {@link ArrayList}
     */
    public ArrayList<HabitEvent> getHabitEvents() {
        return habitEvents;
    }


    /**
     * takes a {@link HabitEvent} to add to {@link Habit#habitEvents}
     * @param habitEvent the habitEvent we wish to associate with the habit
     */
    public void addHabitEvent(HabitEvent habitEvent) {
        habitEvents.add(habitEvent);
    }

    /**
     * clears {@link Habit#habitEvents}
     */
    public void clearHabitEvents(){
        habitEvents.clear();
    }

    /**
     * This function returns {@link Habit#checked}
     * @return The return type is {@link Boolean}
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * This function sets {@link Habit#checked} which is of type {@link Boolean}
     * @param checked the checked status of the habit
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * This function sets {@link Habit#schedule} which is of type {@link HashMap}
     * @param schedule the schedule to follow for the habit
     */
    public void setSchedule(HashMap<String, Boolean> schedule) {
        this.schedule = schedule;
    }

    /**
     * This function gets {@link Habit#schedule} which is of type {@link HashMap}
     */
    public HashMap<String, Boolean> getSchedule() {
        return schedule;
    }
}
