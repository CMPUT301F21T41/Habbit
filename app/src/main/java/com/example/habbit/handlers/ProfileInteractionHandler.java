package com.example.habbit.handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.habbit.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ProfileInteractionHandler {
    /**
     * reference to firestore users collection
     */
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    private static final String TAG = "ProfileInteractHandler";

    /**
     * method called when we want profile information to be sent to firestore
     * @param name, new data used to replace firestore data, of type {@link Map}
     */
    public void updateUser(FirebaseUser user, String name, String email){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"User profile updated");
                        }
                    }
                });

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"User email address updated");
                        }
                    }
                });
    }
}
