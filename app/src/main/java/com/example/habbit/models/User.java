package com.example.habbit.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User extends Profile implements Serializable {

    /**
     * This var is of type {@link String} and contains the username of the current user
     */
    private static String username = "DefaultUser";

    /**
     * type {@String} and contains the password of the current user
     */
    private static String password;

    /**
     * type {@link String} and contains the email of the current user
     */
    private static String email;

    /**
     * This is of type {@link ArrayList<Habit>} and contains the list of {@link Habit} for the user
     */
    private static ArrayList<Habit> userHabits = new ArrayList<>();

    /**
     *
     * @param email
     * provide an email of type {@link String}
     * @param username
     * provide a username of type {@link String}
     * @param password
     * provide a password of type {@link String}
     */
    public User(String email, String username, String password){
    
    }

    public User(){}

    /**
     * This function takes in a habit of type {@link Habit} and adds it to {@link User#userHabits}
     * @param habit
     */
    public static void addHabit(Habit habit){
        userHabits.add(habit);
    }

    /**
     * This function clears {@link User#userHabits}
     */
    public static void clearHabits(){
        userHabits.clear();
    }

    /**
     *
     * @return
     * returns a {@link String} with a formatted view of the User's habits
     */
    public static String printHabits(){
        String ret = "Habits: ";
        for(Habit habit: userHabits){
            ret = ret.concat(habit.getTitle() + " " + habit.getReason() + " " + habit.getDate() + "\r\n");
        }
        return ret;
    }

    /**
     * This function returns {@link User#userHabits}
     * @return
     * The return type is {@link ArrayList}
     */
    public static ArrayList<Habit> getUserHabits(){
        return userHabits;
    }

    /**
     * This function returns {@link User#username}
     * @return
     * The return type is {@link String}
     */
    public static String getUsername() { return username;}


    /**
     * This function sets {@link User#username} which is of type {@link String}
     * @param username
     */
    public static void setUsername(String username){
        User.username = username;
    }


}
