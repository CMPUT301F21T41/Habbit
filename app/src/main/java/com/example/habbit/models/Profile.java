package com.example.habbit.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Profile {

    /**
     * This var is of type {@link String} and contains the unique id of a user
     */
    private String userID;

    /**
     * This var is of type {@link String} and contains the name of a user
     */
    private String name;

    /**
     * This var is of type {@link String} and contains the url of a user's photo
     */
    private String profile_url;

    /**
     * This var is of type {@link Date} and contains the date of birth of a user
     */
    private Date dob;

    // list of friends goes here later

    // list of habits goes here later
    //private List<Habit> myHabits = new ArrayList<>();

    /**
     * This function sets {@link Profile#name} which is of type {@link String}
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * This function returns {@link Profile#name}
     * @return
     * The return type is {@link String}
     */
    public String getName() { return this.name; }

}
