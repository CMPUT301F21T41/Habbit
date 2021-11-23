package com.example.habbit.handlers;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.example.habbit.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class HabitEventInteractionHandler {
    /**
     * This var is of type {@link CollectionReference} and contains the users collection in firestore
     */
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    static final FirebaseStorage storage = FirebaseStorage.getInstance();

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

    public void addHabitEventPhoto(HabitEvent habitEvent, ImageView picture) {
        // Create a storage reference from our app
        StorageReference storageRef;
        storageRef = storage.getReference();

        // Create a reference to "mountains.jpg"
        StorageReference imageRef = storageRef.child(habitEvent.getId()+".jpg");

        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Upload failed in addHabitEventPhoto.");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload success in addHabitEventPhoto.");
            }
        });
    }
}
