package com.example.habbit.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabitFriendList;
import com.example.habbit.models.Habbitor;
import com.example.habbit.models.Habit;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class that controls the display behaviour of a friend's profile page
 */
public class HabbitorProfileActivity extends AppCompatActivity {

    // reference to firestore instance
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    // get reference to elements we'll need throughout the class
    ListView friendHabitListView;
    CustomHabitFriendList friendHabitAdapter;
    ArrayList<Habit> friendHabitList;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        // custom top toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());

        // get username of profile to be displayed
        Intent intent = getIntent();
        Habbitor habbitor = (Habbitor) intent.getSerializableExtra("FRIEND");

        // show username in profile screen
        TextView usernameText = findViewById(R.id.friend_profile_name);
        usernameText.setText(habbitor.getUsername());

        // determine whether or not we have permission to view
        if (habbitor.isFriend()) {
            // get references to UI elements and attach custom adapter
            friendHabitListView = findViewById(R.id.friend_habit_list);
            friendHabitList = habbitor.getPublicHabits();
            friendHabitAdapter = new CustomHabitFriendList(this, friendHabitList);
            friendHabitListView.setAdapter(friendHabitAdapter);

            Log.d("FriendProfile", habbitor.getUserID());
            // get habits
            userCollectionReference.document(habbitor.getUserID()).collection("Habits").addSnapshotListener((value, error) -> {
                // first clear all habits we have currently stored for this friend
                habbitor.clearPublicHabits();

                // pull updated list of habits from firestore
                boolean publicity;
                Map<String, Object> habitData;
                assert value != null;
                for (QueryDocumentSnapshot document : value) {
                    habitData = document.getData();

                    // make sure habit data is not empty before extracting values
                    if (!habitData.isEmpty()) {
                        // set the publicity to true by default
                        if (habitData.get("public") == null) {
                            publicity = true;
                        } else {
                            publicity = (boolean) habitData.get("public");
                        }
                        // only add the habit if it is public
                        if (publicity) {
                            Habit habit = new Habit(Objects.requireNonNull(habitData.get("title")).toString(),
                                    Objects.requireNonNull(habitData.get("reason")).toString(),
                                    Objects.requireNonNull(habitData.get("date")).toString(),
                                    (HashMap<String, Boolean>) Objects.requireNonNull(habitData.get("schedule")),
                                    publicity,
                                    ((Long) Objects.requireNonNull(habitData.get("progress"))).intValue());
                            habit.setId(document.getId());
                            habbitor.addPublicHabit(habit);
                            Log.d("FriendProfile", habbitor.getPublicHabits().get(0).getTitle());
                            Log.d("FriendProfile", "Should be drawing listview");
                        }
                    }
                }

                // redraw listview with newly updated habits
                friendHabitAdapter.notifyDataSetChanged();
            });
        }

    }
}