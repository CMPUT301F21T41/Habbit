package com.example.habbit.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User extends Profile implements Serializable {

    /**
     * Constructor for User object
     *
     * @param email provide an email of type {@link String}
     * @param username provide a username of type {@link String}
     * @param password provide a password of type {@link String}
     */
    public User(String email, String username, String password){

    }

    public User(){}

    /**
     * This var is of type {@link String} and contains the username of the current user
     */
    private static String username = "DefaultUser";

    /**
     * type {@link String} and contains the password of the current user
     */
    private static String password;

    /**
     * type {@link String} and contains the email of the current user
     */
    private static String email;

    private static String userID;

    /**
     * This is of type {@link ArrayList<Habit>} and contains the list of {@link Habit} for the user
     */
    private static ArrayList<Habit> userHabits = new ArrayList<>();

    /**
     * This var is of type {@link ArrayList< Habbitor >} and contains the list of the profile's friends
     */
    private static ArrayList<Habbitor> habbitors = new ArrayList<>();

    /**
     * This var is of type {@link Map<Habbitor, Integer>} and contains the relationship to each habbitor
     */
    private static HashMap<String, Integer> relationships = new HashMap<>();

    /**
     * Registers a relationship between habbitor and current user
     *
     * @param habbitorID The habbitorID whose relationship to add or update
     * @param relationship The relationship of that habbitor to the current user
     */
    public static void addRelationship(String habbitorID, Integer relationship) {
        relationships.put(habbitorID, relationship);
    }

    /**
     * Get the relationships
     *
     * @return Relationships of each habbitor to the current user
     */
    public static HashMap<String, Integer> getRelationships() {
        return relationships;
    }

    /**
     * This function takes in a habit of type {@link Habit} and adds it to {@link User#userHabits}
     * @param habit habit to add to user habits
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
     * This function returns {@link User#userHabits}
     * @return The return type is {@link ArrayList}
     */
    public static ArrayList<Habit> getUserHabits(){
        return userHabits;
    }

    /**
     * This function adds a friend to the users list of friends
     *
     * @param habbitor The {@link Habbitor} we want to add
     */
    public static void addHabbitor(Habbitor habbitor) {
        habbitors.add(habbitor);
    }

    /**
     * This function clears the {@link User#habbitors}
     */
    public static void clearHabbitors() {
        habbitors.clear();
    }

    /**
     * Gets the users friends
     * @return The return type is {@link ArrayList< Habbitor >}
     */
    public static ArrayList<Habbitor> getHabbitors() {
        return habbitors;
    }

    /**
     * This function returns {@link User#username}
     * @return The return type is {@link String}
     */
    public static String getUsername() { return username;}


    /**
     * This function sets {@link User#username} which is of type {@link String}
     * @param username the username to give the user
     */
    public static void setUsername(String username){
        User.username = username;
    }

    public static void setUserID(String userID) {
        User.userID = userID;
    }

    public static String getUserID() {
        return User.userID;
    }

    public static void setRelationships(HashMap<String, Integer> relationships) {
        User.relationships = relationships;
    }

}
