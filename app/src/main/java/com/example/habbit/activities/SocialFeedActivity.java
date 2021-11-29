package com.example.habbit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomFriendList;
import com.example.habbit.models.Friend;
import com.example.habbit.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class SocialFeedActivity extends AppCompatActivity {
    // references to entities used throughout the class
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    FirebaseAuth userAuth;
    FirebaseUser user;
    String userID;

    // adapters and arraylist for friend list
    CustomFriendList friendAdapter;
    ArrayList<Friend> friends;
    ListView friendListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);
        userAuth = FirebaseAuth.getInstance();

        // custom top toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());

        // If no one is logged in, set the current user to the default
        user = userAuth.getCurrentUser();
        if(user==null){
            userID = "AlF39kSveNM3BYaUmSQfWqvtsxt1";
        } else {
            userID = user.getUid();
        }

        // get references to UI elements and attach custom adapter
        friends = User.getUserFriends();
        friendAdapter = new CustomFriendList(this, friends);
        friendListView = findViewById(R.id.friend_list);
        friendListView.setAdapter(friendAdapter);

        // listener for friends list that will open a Profile view when a Habit is selected
        friendListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Friend friend = (Friend) friendAdapter.getItem(i);

            // open the friend profile
            Intent intent = new Intent(this, FriendProfileActivity.class);
            intent.putExtra("FRIEND", friend);
            startActivity(intent);
        });

        // refresh the listview every time we update Firestore
        userCollectionReference.addSnapshotListener((value, error) -> {
            // we first clear all the friends we have currently stored for the user
            User.clearFriends();

            Map<String, Object> userData;
            // get all users
            for(QueryDocumentSnapshot document: value) {
                userData = document.getData();
                if (!userData.isEmpty()) {
                    String name = (String) userData.get("Username");
                    String userID = (String) userData.get("User ID");
                    if (name != null && !name.equals(user.getDisplayName())) {
                        Log.d("SocialFeedActivity", "Getting User name " + name + userData.get("User ID"));
                        Friend friend = new Friend(name, (String) userID);

                        User.addFriend(friend);
                    }
                }
            }

            // redraw listview
            friendAdapter.notifyDataSetChanged();
        });

    }
}