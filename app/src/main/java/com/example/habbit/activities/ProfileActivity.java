package com.example.habbit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habbit.fragments.ProfileEntryFragment;
import com.example.habbit.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Class for profile view {@link AppCompatActivity}
 * Opened from {@link MainActivity}
 */
public class ProfileActivity extends AppCompatActivity
        implements ProfileEntryFragment.OnProfileEntryFragmentInteractionListener {


    /**
     * reference to firestore users collection
     */
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    /**
     * forward declaration of username for user logged in.
     */
    String userLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get username of user logged in
        Intent intent = getIntent();
        userLoggedIn = (String) intent.getSerializableExtra("USER");
        Map<String,Object> userData = new HashMap<>();

        //show username in profile screen
        TextView userText = findViewById(R.id.username_text);
        userText.setText(userLoggedIn);
        //show name in profile screen
        TextView emailText = findViewById(R.id.name_text);

        //listen for changes to firestore and get new data to display
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        userDoc.addSnapshotListener((value, error) -> {
            assert value != null;
            if (value.get("Email") != null) {
                        userData.put("Email", Objects.requireNonNull(value.get("Email")).toString());
                        emailText.setText(Objects.requireNonNull(value.get("Email")).toString());

                    }
                    if(value.get("Name") != null) {
                        userData.put("Name", Objects.requireNonNull(value.get("Name")).toString());
                    }
                });

            //open edit profile fragment on button press, pass user data to display
            final Button editProfileButton = findViewById(R.id.edit_profile_button);
            editProfileButton.setOnClickListener(view -> ProfileEntryFragment.newInstance(userData)
                    .show(getSupportFragmentManager(), "EDIT_PROFILE"));


        }

    /**
     * method called when profile edit is confirmed
     * @param userData, new data used to replace firestore data, of type {@link Map}
     */
    @Override
    public void onEditProfilePressed(Map<String,Object> userData){
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        userDoc.update(userData);
    }

}