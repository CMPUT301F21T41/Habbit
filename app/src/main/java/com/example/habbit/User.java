package com.example.habbit;

import java.util.ArrayList;

public class User extends Profile {
    private String username;
    private String password;
    private String email;

    private ArrayList<Habit> userHabits = new ArrayList<>();

    public User(String email, String username, String password){
    
    }

    public User(){}

    public void addHabit(Habit habit){
        this.userHabits.add(habit);
    }

    public void clearHabit() {
        this.userHabits.clear();
    }


    public boolean checkForHabits(String habitTitle){
        for(Habit habit : this.userHabits){
            if(habit.getTitle().equals(habitTitle)){
                return true;
            }
        }
        return false;
    }

    public void clearHabits(){
        userHabits.clear();
    }

    public String printHabits(){
        String ret = "Habits: ";
        for(Habit habit: this.userHabits){
            ret = ret.concat(habit.getTitle() + " " + habit.getReason() + " " + habit.getDate() + "\r\n");
        }
        return ret;
    }
    public ArrayList<Habit> getUserHabits(){
        return this.userHabits;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setEmail(String email) { this.email = email; }




}
