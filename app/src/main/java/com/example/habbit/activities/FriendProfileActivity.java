package com.example.habbit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabitFriendList;
import com.example.habbit.adapters.CustomHabitList;
import com.example.habbit.models.Friend;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.habbit.models.Habit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FriendProfileActivity extends AppCompatActivity {
    /**
     * reference to firestore users collection
     */
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    ListView friendHabitListView;
    CustomHabitFriendList friendHabitAdapter;
    ArrayList<Habit> friendHabitList;

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
        Friend friend = (Friend) intent.getSerializableExtra("FRIEND");

        // show username in profile screen
        TextView usernameText = findViewById(R.id.friend_profile_name);
        usernameText.setText(friend.getUsername());

        // get references to UI elements and attach custom adapter
        friendHabitListView = findViewById(R.id.friend_habit_list);
        friendHabitList = friend.getPublicHabits();
        friendHabitAdapter = new CustomHabitFriendList(this, friendHabitList);
        friendHabitListView.setAdapter(friendHabitAdapter);

        Log.d("FriendProfile", friend.getUserID());
        // get habits
        userCollectionReference.document(friend.getUserID()).collection("Habits").addSnapshotListener((value, error) -> {
            // first clear all habits we have currently stored for this friend
            friend.clearPublicHabits();

            // pull updated list of habits from firestore
            boolean publicity;
            Map<String, Object> habitData;
            assert value != null;
            for (QueryDocumentSnapshot document: value) {
                Log.d("FriendProfile", "It has data!");
                habitData = document.getData();
                if (!habitData.isEmpty()) {
                    Log.d("FriendProfile", "It is not empty!");
                    // TODO: why this way
                    if (habitData.get("public") == null) {
                        publicity = true;
                    } else {
                        publicity = (boolean) habitData.get("public");
                    }

                    Log.d("FriendProfile", "Publicity: " + String.valueOf(publicity));
                    if (publicity) {
                        Habit habit = new Habit(Objects.requireNonNull(habitData.get("title")).toString(),
                                Objects.requireNonNull(habitData.get("reason")).toString(),
                                Objects.requireNonNull(habitData.get("date")).toString(),
                                (HashMap<String, Boolean>) Objects.requireNonNull(habitData.get("schedule")),
                                publicity);
                        habit.setId(document.getId());
                        friend.addPublicHabit(habit);
                        Log.d("FriendProfile", friend.getPublicHabits().get(0).getTitle());
                        Log.d("FriendProfile", "Should be drawing listview");
                    }
                }
            }

            // redraw listview with newly updated habits
            friendHabitAdapter.notifyDataSetChanged();
        });
    }
}