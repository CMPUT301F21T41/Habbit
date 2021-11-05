package com.example.habbit.handlers;

import com.example.habbit.models.Habit;
import com.example.habbit.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HabitInteractionHandler {
    /**
     * This var is of type {@link CollectionReference} and contains the users collection in firestore
     */
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    /**
     * This var is of type {@link String} and contains the username
     */
    String username;

    /**
     * sets the username to the current user's username
     */
    public HabitInteractionHandler() {
        username = User.getUsername();
    }

    /**
     *
     * @param habit
     * given a habit of type {@link Habit}, add it to the database
     */
    public void addHabit(Habit habit) {
        DocumentReference userDoc = userCollectionReference.document(username);
        // adding a habit using an autogenerated ID rather than using titletext
        userDoc.collection("Habits")
                .add(habit);
    }

    /**
     *
     * @param newHabit
     * given an updated habit of type {@link Habit}, update it in the database
     */
    public void updateHabit(Habit newHabit){
        // get the updated values
        String titleText = newHabit.getTitle();
        String reasonText = newHabit.getReason();
        String dateText = newHabit.getDate();
        boolean isChecked = newHabit.isChecked();

        // update the firestore
        DocumentReference userDoc = userCollectionReference.document(username);
        userDoc.collection("Habits").document(newHabit.getId())
                .update("title", titleText,
                        "reason", reasonText,
                        "date", dateText,
                        "checked", isChecked);
    }

    /**
     *
     * @param habit
     * given a {@link Habit} to delete, delete it from the database
     */
    public void deleteHabit(Habit habit){
        // TODO: Gonna need to figure out how to delete subcollections too, but not yet
        // doesn't really affect the app, just clutters up our firestore
        DocumentReference userDoc = userCollectionReference.document(username);
        userDoc.collection("Habits").document(habit.getId())
                .delete();
    }
}
