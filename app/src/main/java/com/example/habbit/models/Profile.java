package com.example.habbit.models;

import java.util.Date;

/**
 * Profile class contains all information need for social profiles on Habbit
 */
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

    /**
     * This function sets {@link Profile#name} which is of type {@link String}
     * @param name the name we wish to give the profile
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
