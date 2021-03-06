package com.example.habbit.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.habbit.R;
import com.example.habbit.fragments.ProfileEntryFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Class for profile view {@link AppCompatActivity}
 * Opened from {@link MainActivity}
 */
public class ProfileActivity extends AppCompatActivity {

    /**
     * forward declaration of username for user logged in.
     */
    FirebaseUser userLoggedIn;
    FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userAuth = FirebaseAuth.getInstance();

        // custom top toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());

        // get username of user logged in
        userLoggedIn = userAuth.getCurrentUser();

        //show username in profile screen
        TextView userText = findViewById(R.id.username_text);
        String userName = userLoggedIn.getDisplayName();
        if (userName==null){
            userName = "No Set Username";
        }
        userText.setText(userName);

        userText.setText(userLoggedIn.getDisplayName());
        //show email in profile screen
        TextView emailText = findViewById(R.id.name_text);
        emailText.setText(userLoggedIn.getEmail());

        //open edit profile fragment on button press, pass user data to display
        final Button editProfileButton = findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(v -> {
            ProfileEntryFragment.newInstance().show(getSupportFragmentManager(),"EDIT_PROFILE");
            userText.setText(userLoggedIn.getDisplayName());
            emailText.setText(userLoggedIn.getEmail());
        });

    }

}