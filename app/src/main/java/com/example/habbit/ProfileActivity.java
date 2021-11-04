package com.example.habbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity
        implements ProfileEntryFragment.OnProfileEntryFragmentInteractionListener {

    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    DocumentSnapshot docSnap;

    String userLoggedIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        //User user = new User();
        userLoggedIn = (String) intent.getSerializableExtra("USER");
        Map<String,Object> userData = new HashMap<>();
        //String[] email = new String[1];


        TextView userText = (TextView) findViewById(R.id.username_text);
        userText.setText(userLoggedIn);

        TextView nameText = findViewById(R.id.name_text);

        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);


        userDoc.addSnapshotListener((value, error) -> {
                    Toast.makeText(ProfileActivity.this, "Success on firestore read", Toast.LENGTH_SHORT).show();
                    if (value.get("Email") != null) {
                        userData.put("Email", value.get("Email").toString());
                        userData.put("Name", value.get("Name").toString());
                        nameText.setText(value.get("Name").toString());

                    }
                });


            final Button editProfileButton = findViewById(R.id.edit_profile_button);
            editProfileButton.setOnClickListener(view -> ProfileEntryFragment.newInstance(userData)
                    .show(getSupportFragmentManager(), "EDIT_PROFILE"));


        }

    @Override
    public void onEditProfilePressed(Map<String,Object> userData){
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        //Map<String,Object> map =  userData;
        userDoc.update(userData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(documentReference -> {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT);
                });
    }













}