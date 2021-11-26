package com.example.habbit.handlers;

import com.example.habbit.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ProfileInteractionHandler {
    /**
     * reference to firestore users collection
     */
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");


    /**
     * method called when we want profile information to be sent to firestore
     * @param userData, new data used to replace firestore data, of type {@link Map}
     */
    public void updateUser(Map<String,Object> userData){
        DocumentReference userDoc = userCollectionReference.document(User.getUsername());
        userDoc.update(userData);
    }
}
