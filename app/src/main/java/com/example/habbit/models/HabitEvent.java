package com.example.habbit.models;

import java.io.Serializable;

public class HabitEvent implements Serializable {
    // TODO: completedOnTime can't be done until we implement selecting days we want to do habits which is in our next milestone
    /**
     * This var is of type {@link Boolean} and denotes whether or not the {@link Habit} was completed on time
     */
    private boolean completedOnTime;
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
     * This var is of type {@link String} and contains the date that the habit event was completed
     */
    private String dateCompleted; // TODO: would be cool to know what date the habit event was completed


    /**
     *
     * @param comment
     * pass in the comment
     */
    public HabitEvent(String comment) {
        this.comment = comment;
    }

    /**
     *
     * @param completedOnTime
     * pass in if the {@link Habit} was completed on time
     * @param comment
     * provide a comment
     */
    public HabitEvent(boolean completedOnTime, String comment) {
        this.completedOnTime = completedOnTime;
        this.comment = comment;
    }

    /**
     * This function returns {@link HabitEvent#completedOnTime}
     * @return
     * The return type is {@link Boolean}
     */
    public boolean isCompletedOnTime() {
        return completedOnTime;
    }

    /**
     * This function returns {@link HabitEvent#id}
     * @return
     * The return type is {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * This function sets {@link HabitEvent#id} which is of type {@link String}
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This function returns {@link HabitEvent#comment}
     * @return
     * The return type is {@link String}
     */
    public String getComment() {
        return comment;
    }

    /**
     * This function sets {@link HabitEvent#comment} which is of type {@link String}
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
