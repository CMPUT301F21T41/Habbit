package com.example.habbit.handlers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.example.habbit.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HabitEventInteractionHandler {
    /**
     * This var is of type {@link CollectionReference} and contains the users collection in firestore
     */
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    /**
     * This var is of type {@link String} and contains the username
     */
    String username;

    /**
     * This var is of type {@link HabitInteractionHandler} and is an instantiation of the object that handles {@link Habit} operations
     */
    HabitInteractionHandler habitHandler;

    /**
     * This var is of type {@link Habit} and is an instantiation of the habit selected
     */
    Habit habit;

    /**
     * Constructor for the interaction handler
     * @param habit provided a {@link Habit}, initialize everything based on it's attributes
     */
    public HabitEventInteractionHandler(Habit habit) {
        username = User.getUsername();
        habitHandler = new HabitInteractionHandler();
        this.habit = habit;
    }

    /**
     * Adds a habitevent to the database
     * @param habitEvent the {@link HabitEvent} to be added to the firestore database
     */
    public void addHabitEvent(@Nullable HabitEvent habitEvent) {
        DocumentReference userDoc = userCollectionReference.document(username);
        assert habitEvent != null;

        // set the habit checked value to true since we have logged a habit event for the day
        habit.setChecked(true);
        habitHandler.updateHabit(habit);

        // update firestore with the new habit event
        userDoc.collection("Habits").document(habit.getId())
                .collection("Habit Events").add(habitEvent);
    }

    /**
     * Deletes a habit event from the database
     * @param habitEvent the {@link HabitEvent} to be added to the database
     */
    public void deleteHabitEvent(HabitEvent habitEvent){
        Log.d("HabitEventHandler", "entered delete habit event");
        Log.d("HabitEventHandler", habitEvent.getId());
        DocumentReference userDoc = userCollectionReference.document(username)
                .collection("Habits").document(habit.getId());
        userDoc.collection("Habit Events").document(habitEvent.getId())
                .delete();
    }

    /**
     * Updates the habitevent in the database
     * @param newHabitEvent the {@link HabitEvent} to be updated
     */
    public void updateHabitEvent(@Nullable HabitEvent newHabitEvent) {
        // get updated values
        assert newHabitEvent != null;
        String commentText = newHabitEvent.getComment();

        // update FireStore
        DocumentReference userDoc = userCollectionReference.document(username)
                .collection("Habits").document(habit.getId());
        userDoc.collection("Habit Events").document(newHabitEvent.getId())
                .update("comment", commentText);
        Log.d(TAG, newHabitEvent.getId());
    }
}
