package com.example.habbit.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabbitorList;
import com.example.habbit.handlers.UserInteractionHandler;
import com.example.habbit.models.Habbitor;
import com.example.habbit.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SocialFeedActivity extends AppCompatActivity {
    // references to entities used throughout the class
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    FirebaseAuth userAuth;
    FirebaseUser user;
    String userID;

    // adapters and arraylist for friend list
    CustomHabbitorList habbitorAdapter;
    ArrayList<Habbitor> habbitors;
    ListView habbitorListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);
        userAuth = FirebaseAuth.getInstance();
        UserInteractionHandler handler = new UserInteractionHandler();

        // custom top toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());

        // get button to navigate to follow requests
        Button button = findViewById(R.id.view_follow_requests);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FollowRequestsActivity.class);
            startActivity(intent);
        });

        // If no one is logged in, set the current user to the default
        user = userAuth.getCurrentUser();
        if(user==null){
            userID = "AlF39kSveNM3BYaUmSQfWqvtsxt1";
        } else {
            userID = user.getUid();
        }

        // get references to UI elements and attach custom adapter
        habbitors = User.getHabbitors();
        habbitorAdapter = new CustomHabbitorList(this, habbitors);
        habbitorListView = findViewById(R.id.habbitor_list);
        habbitorListView.setAdapter(habbitorAdapter);

        // listener for friends list that will open a Profile view when a Habit is selected
        habbitorListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Habbitor habbitor = (Habbitor) habbitorAdapter.getItem(i);

            // open the friend profile
            Intent intent = new Intent(this, FriendProfileActivity.class);
            intent.putExtra("FRIEND", habbitor);
            startActivity(intent);
        });

        // refresh the listview every time we update Firestore
        userCollectionReference.addSnapshotListener((value, error) -> {
            DocumentReference docRef = userCollectionReference.document(user.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                    // we first clear all the friends we have currently stored for the user
                    User.clearHabbitors();
                    User.clearRelationships();

                    HashMap<String, Integer> relationships;
                    if (documentSnapshot.get("Relationships") == null) {
                        relationships = new HashMap<String, Integer>();
                    } else {
                        relationships = (HashMap<String, Integer>) documentSnapshot.get("Relationships");
                    }
                    Map<String, Object> userData;
                    // get all users
                    for(QueryDocumentSnapshot document: value) {
                        userData = document.getData();
                        if (!userData.isEmpty()) {
                            String name = (String) userData.get("Username");
                            String userID = (String) userData.get("User ID");
                            Log.d("TAAAAG", user.getDisplayName());
                            if (name != null && !name.equals(user.getDisplayName())) {
                                Log.d("SocialFeedActivity", "Getting User name " + name + userData.get("User ID"));
                                Habbitor habbitor = new Habbitor(name, userID);
                                Object relationObject = relationships.get(userID);
                                Integer relationshipToUser;
                                if (relationObject == null) {
                                    // default relationship is stranger
                                    relationshipToUser = Habbitor.relationshipTypes.get("Stranger");
                                } else {
                                    relationshipToUser = Integer.valueOf(String.valueOf(relationObject));
                                }
                                User.addRelationship(userID, relationshipToUser);
                                habbitor.setRelationship(relationshipToUser);
                                User.addHabbitor(habbitor);
                            }
                        }
                    }

                    // update the user relationships map in firestore so we have it for next time
                    handler.updateUserRelationships(user.getUid(), relationships);
                    // redraw listview
                    habbitorAdapter.notifyDataSetChanged();

                }
            });
        });
    }
}