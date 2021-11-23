package com.example.habbit.models;

import java.io.Serializable;

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
     * This var is of type {@link String} and contains the date that the habit event was completed
     */
    private String dateCompleted; // TODO: would be cool to know what date the habit event was completed


    /**
     * HabitEvent constructor
     *
     * @param comment pass in the comment
     */
    public HabitEvent(String comment) {
        this.comment = comment;
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
}
