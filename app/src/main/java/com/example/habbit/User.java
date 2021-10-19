package com.example.habbit;

import java.util.ArrayList;
import java.util.List;

public class User extends Profile {
    private String username;
    private String password;

    private List<Habit> myHabits = new ArrayList<>();

    public void addHabit(Habit habit){
        this.myHabits.add(habit);
    }




}
