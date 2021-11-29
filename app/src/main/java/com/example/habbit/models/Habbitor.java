package com.example.habbit.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.habbit.models.Habit;

/**
 * This Class represents the profile of a Friend
 * A friend is considered to be someone the user has submitted a follow request to
 */
public class Habbitor extends Profile implements Serializable {

    public Habbitor(String username, String userID) {
        this.username = username;
        this.userID = userID;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Habbitor) {
            Habbitor habbitor = (Habbitor) object;
            if (habbitor.getUserID().equals(this.getUserID())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    // a friend is a habbitor who has accepted the current user's follow request
    // an acquaintance is someone who has not yet accepted the current user's follow request
    // a stranger is someone who the current user has not submitted a follow request to
    public static final Map<String, Integer> relationshipTypes = new HashMap<String, Integer>() {{
        put("Friend", 0);
        put("Acquaintance", 1);
        put("Stranger", 2);
    }};

    // default relationship will be stranger
    private int relationship = relationshipTypes.get("Stranger");

    private ArrayList<Habit> publicHabits = new ArrayList<>();

    private String username;

    private String userID;

    public boolean isStranger() {
        return relationship == relationshipTypes.get("Stranger");
    }

    public boolean isFriend() {
        return relationship == relationshipTypes.get("Friend");
    }

    public boolean isAcquaintance() {
        return relationship == relationshipTypes.get("Acquaintance");
    }

    public void setRelationship(Integer relationship) {
        if (relationship != null) {
            this.relationship = relationship;
        } else {
            this.relationship = relationshipTypes.get("Stranger");
        }
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
