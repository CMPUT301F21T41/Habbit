package com.example.habbit.activities;

import static android.content.ContentValues.TAG;
import static com.example.habbit.activities.MainActivity.userCollectionReference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabitEventList;
import com.example.habbit.fragments.HabitEventDetailsFragment;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.example.habbit.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * This class is responsible for displaying the habit events screen
 */
public class HabitEventsActivity extends AppCompatActivity {

    // get entities to be used throughout the class
    CustomHabitEventList habitEventAdapter;
    ArrayList<HabitEvent> habitEventDataList;
//    String username;
    FirebaseAuth userAuth;
    FirebaseUser user;
    String userID;
    Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_events);
        userAuth = FirebaseAuth.getInstance();
        user = userAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24)); // change to getDrawable(int, Theme)
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // initialize views
        ListView habitEventList = findViewById(R.id.habit_events_list);

        // get the habit containing desired habit events
        habit = (Habit) getIntent().getSerializableExtra("habit");
        habitEventDataList = habit.getHabitEvents();

        // set the adapter
        habitEventAdapter = new CustomHabitEventList(this, habitEventDataList, habit);
        habitEventList.setAdapter(habitEventAdapter);

        // get user login
        userID = user.getUid();

        // instantiate a listener for habitList that will open a HabitDetailsFragment when a Habit is selected
        habitEventList.setOnItemClickListener((adapterView, view, i, l) -> {
            HabitEvent habitEvent = (HabitEvent) habitEventAdapter.getItem(i);
            HabitEventDetailsFragment.newInstance(habitEvent, habit).show(getSupportFragmentManager(),"VIEW_HABIT_EVENT");
        });

        // initialize/update the list every time there is a change made to the habit events
        userCollectionReference.document(userID).collection("Habits").document(habit.getId())
                .collection("Habit Events").addSnapshotListener((value, error) -> {
                    habit.clearHabitEvents();
                    Map<String,Object> habitEventData;
            assert value != null;
            for(QueryDocumentSnapshot document:value) {
                        habitEventData = document.getData();
                        Log.d(TAG, document.getId() + " => " + habitEventData);
                        if (!habitEventData.isEmpty()) {
                            // every time we pull from Firestore, get the document ID data and associate it with the HabitEvent object
                            Log.d("seans", habitEventData.get("comment").toString() + habitEventData);
                            HabitEvent habitEvent = new HabitEvent(Objects.requireNonNull(habitEventData.get("comment")).toString(),
                                    Objects.requireNonNull(habitEventData.get("imageURL")).toString(),
                                    (double) Objects.requireNonNull(habitEventData.get("latitude")),
                                    (double) Objects.requireNonNull(habitEventData.get("longitude")),
                                    Objects.requireNonNull(habitEventData.get("city").toString()),
                                    Objects.requireNonNull(habitEventData.get("province")).toString());
                            Log.d("seans", Objects.requireNonNull(habitEventData.get("comment")).toString()
                                                        + habitEvent.getLatitude() + habitEvent.getLongitude());

                            habitEvent.setId(document.getId());
                            habit.addHabitEvent(habitEvent);
                        }
                    }
                    // update the data list
                    habitEventDataList = habit.getHabitEvents();

                    // notify the adapter
                    habitEventAdapter.notifyDataSetChanged();
                });

    }

}