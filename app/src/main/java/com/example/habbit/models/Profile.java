package com.example.habbit.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Profile {
    private String userID;
    private String name;
    private String profile_url; // stores url of user's photo, not sure how else to store image
    private Date dob;

    // list of friends goes here later

    // list of habits goes here later
    //private List<Habit> myHabits = new ArrayList<>();

    public void setName(String name){
        this.name = name;
    }
}
