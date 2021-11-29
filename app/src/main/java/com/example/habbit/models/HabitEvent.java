package com.example.habbit.models;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HabitEvent implements Serializable {
    // TODO: something that holds an image attachment

    // TODO: something that holds geolocation

    /**
     * This var is of type {@link String} and contains additional comments of a logged event
     */
    private String comment;

    /**
     * This var is of type {@link String} and contains the unique id of a {@link HabitEvent}
     */
    private String id;

    /**
     * This var is of type {@link double} and contains the position in terms of lonitude of a {@link HabitEvent}
     */
    private double longitude = 0;

    /**
     * This var is of type {@link double} and contains the position in terms of latitude of a {@link HabitEvent}
     */
    private double latitude = 0;


    private String imageURL;

    /**
     * This var is of type {@link String} and contains the date that the habit event was completed
     */
    private String dateCompleted;


    /**
     * HabitEvent constructor
     *
     * @param comment pass in the comment
     */
    public HabitEvent(String comment) {
        this.comment = comment;
    }

    /**
     * This function returns {@link HabitEvent#dateCompleted} in Date Object form
     * @return The return type is {@link Date}
     */
    @SuppressLint("SimpleDateFormat")
    public Date getDateObject() {
        Date dateObj = null;
        try {
            dateObj = new SimpleDateFormat("yyyy-MM-dd").parse(dateCompleted);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateObj;
    }

    /**
     * HabitEvent lat/lon constructor
     *
     * @param comment of type{@link String} pass in comment
     * @param latitude of type {@link double} pass latitude of location
     * @param longitude of type {@link double} pass longitude of location
     */
    public HabitEvent(String comment, String imageURL, double latitude,double longitude, String dateCompleted ){
        this.comment = comment;
        this.imageURL = imageURL;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCompleted = dateCompleted;
    }

    /**
     * This function returns {@link HabitEvent#id}
     * @return The return type is {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * This function sets {@link HabitEvent#id} which is of type {@link String}
     * @param id the id we wish to give to the habitevent
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This function returns {@link HabitEvent#comment}
     * @return The return type is {@link String}
     */
    public String getComment() {
        return comment;
    }

    /**
     * This function sets {@link HabitEvent#comment} which is of type {@link String}
     * @param comment the comment we wish to give to the habit event
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * This function returns {@link HabitEvent#dateCompleted}
     * @return The return type is {@link String}
     */
    public String getDate() {
        return dateCompleted;
    }

    /**
     * This function sets {@link HabitEvent#dateCompleted} which is of type {@link String}
     * @param date The date we want to give to the habit
     */
    public void setDate(String date) {
        this.dateCompleted = date;
    }
}
