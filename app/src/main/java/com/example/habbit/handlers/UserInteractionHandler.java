package com.example.habbit.handlers;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.habbit.models.Habbitor;
import com.example.habbit.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInteractionHandler {
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

        // update firestore instance
        Map<String,Object> userData = new HashMap<>();
        userData.put("Username", name);
        userCollectionReference.document(user.getUid())
                .set(userData);
    }

    public void updateUserRelationships(String userID, HashMap<String, Integer> relationships) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("Relationships", relationships);
        userCollectionReference.document(userID).update(userData);
    }

    public void addRequest(String userID, String habbitorID) {
        DocumentReference docRef = userCollectionReference.document(habbitorID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                ArrayList<String> requests;
                if (documentSnapshot.get("Requests") == null) {
                    requests = new ArrayList<String>();
                } else {
                    requests = (ArrayList<String>) documentSnapshot.get("Requests");
                }
                requests.add(userID);
                Map<String, Object> userData = new HashMap<>();
                userData.put("Requests", requests);
                userCollectionReference.document(habbitorID).update(userData);
            }
        });
    }

    public void displayName(String habbitorID, TextView nameView) {
        userCollectionReference.document(habbitorID)
                .get().addOnSuccessListener(documentSnapshot -> {
            String name = (String) documentSnapshot.get("Username");
            nameView.setText(name);
        });
    }

    public void acceptRequest(String userID, String habbitorID) {
        // change relationship from habbitor to user as friend
        Integer newRelationship = Habbitor.relationshipTypes.get("Friend");

        DocumentReference docRef = userCollectionReference.document(habbitorID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                HashMap<String, Integer> relationships;
                if (documentSnapshot.get("Relationships") == null) {
                    relationships = new HashMap<String, Integer>();
                } else {
                    relationships = (HashMap<String, Integer>) documentSnapshot.get("Relationships");
                }
                relationships.put(userID, newRelationship);
                Map<String, Object> userData = new HashMap<>();
                userData.put("Relationships", relationships);
                userCollectionReference.document(habbitorID).update(userData);
            }
        });
    }

}
