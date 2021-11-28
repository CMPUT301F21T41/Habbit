package com.example.habbit.models;

/**
 * This Class represents the profile of a Friend
 */
public class Friend extends Profile {

    private boolean isFriend;

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }
}
