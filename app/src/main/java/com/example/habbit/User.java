package com.example.habbit;

import java.util.ArrayList;
import java.util.List;

public class User extends Profile {
    private String username;
    private String password;

    private ArrayList<Habit> myHabits = new ArrayList<>();

    public void addHabit(Habit habit){
        this.myHabits.add(habit);
    }

    public boolean checkForHabits(String habitTitle){
        for(Habit habit : this.myHabits){
            if(habit.getTitle().equals(habitTitle)){
                return true;
            }
        }
        return false;
    }

    public void clearHabits(){
        myHabits.clear();
    }

    public String printHabits(){
        String ret = "Habits: ";
        for(Habit habit: this.myHabits){
            ret = ret.concat(habit.getTitle() + " " + habit.getReason() + " " + habit.getDate() + "\r\n");
        }
        return ret;
    }
    public ArrayList<Habit> getMyHabits(){
        return this.myHabits;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }




}
