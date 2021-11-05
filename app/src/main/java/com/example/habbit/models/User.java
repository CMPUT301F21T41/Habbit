package com.example.habbit.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User extends Profile implements Serializable {
    private static String username = "DefaultUser";
    private static String password;
    private static String email;

    private static ArrayList<Habit> userHabits = new ArrayList<>();

    public User(String email, String username, String password){
    
    }

    public User(){}

    public static void addHabit(Habit habit){
        userHabits.add(habit);
    }

    public void clearHabit() {
        userHabits.clear();
    }


    public boolean checkForHabits(String habitTitle){
        for(Habit habit : userHabits){
            if(habit.getTitle().equals(habitTitle)){
                return true;
            }
        }
        return false;
    }

    public static void clearHabits(){
        userHabits.clear();
    }

    public static String printHabits(){
        String ret = "Habits: ";
        for(Habit habit: userHabits){
            ret = ret.concat(habit.getTitle() + " " + habit.getReason() + " " + habit.getDate() + "\r\n");
        }
        return ret;
    }
    public static ArrayList<Habit> getUserHabits(){
        return userHabits;
    }
    public static String getUsername() { return username;}



    public static void setUsername(String username){
        User.username = username;
    }
    public void setPassword(String password){
        User.password = password;
    }
    public void setEmail(String email) { User.email = email; }





}
