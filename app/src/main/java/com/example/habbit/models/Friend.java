package com.example.habbit.models;

import java.io.Serializable;
import java.util.ArrayList;
import com.example.habbit.models.Habit;

/**
 * This Class represents the profile of a Friend
 * A friend is considered to be someone the user has submitted a follow request to
 */
public class Friend extends Profile implements Serializable {

    public Friend(String username, String userID) {
        this.username = username;
        this.userID = userID;
    }

    private ArrayList<Habit> publicHabits = new ArrayList<>();

    private String username;

    private String userID;

    private boolean isFriend;

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUserID() {
        return this.userID;
    }

    public ArrayList<Habit> getPublicHabits() {
        return publicHabits;
    }

    public void addPublicHabit(Habit habit) {
        publicHabits.add(habit);
    }

    public void clearPublicHabits() {
        publicHabits.clear();
    }
}
