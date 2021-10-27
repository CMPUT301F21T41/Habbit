package com.example.habbit;

import java.util.ArrayList;

public class User extends Profile {
    private String username;
    private String password;

    private ArrayList<Habit> userHabits = new ArrayList<>();

    public void addHabit(Habit habit){
        this.userHabits.add(habit);
    }

    public void clearHabit() {
        this.userHabits.clear();
    }

//    public void deleteHabit(Habit habit) {
//        this.myHabits.remove(habit);
//    }

//    //Bugged for some reason
//    public void editHabit(Habit ogHabit, Habit newHabit) {
//        this.myHabits.set(this.myHabits.indexOf(ogHabit), newHabit);
//    }

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




}
