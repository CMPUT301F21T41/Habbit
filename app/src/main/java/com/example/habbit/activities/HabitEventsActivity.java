package com.example.habbit.activities;

import static android.content.ContentValues.TAG;
import static com.example.habbit.activities.MainActivity.userCollectionReference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabitEventList;
import com.example.habbit.fragments.HabitEventDetailsFragment;
import com.example.habbit.fragments.HabitEventEntryFragment;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.example.habbit.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HabitEventsActivity extends AppCompatActivity
        implements HabitEventDetailsFragment.OnHabitEventDetailInteraction,
        HabitEventEntryFragment.OnHabitEventFragmentInteractionListener {

    // get entities to be used throughout the class
    CustomHabitEventList habitEventAdapter;
    ArrayList<HabitEvent> habitEventDataList;
    String username;
    Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_events);

        // initialize views
        ListView habitEventList = findViewById(R.id.habit_events_list);

        // get the habit containing desired habit events
        habit = (Habit) getIntent().getSerializableExtra("habit");
        habitEventDataList = habit.getHabitEvents();

        // set the adapter
        habitEventAdapter = new CustomHabitEventList(this, habitEventDataList);
        habitEventList.setAdapter(habitEventAdapter);

        // GET USER LOGIN -- ADD LATER
        username = User.getUsername();

        // instantiate a listener for habitList that will open a HabitDetailsFragment when a Habit is selected
        habitEventList.setOnItemClickListener((adapterView, view, i, l) -> {
            HabitEvent viewHabitEvent = (HabitEvent) habitEventAdapter.getItem(i);
            HabitEventDetailsFragment.newInstance(viewHabitEvent).show(getSupportFragmentManager(),"VIEW_HABIT_EVENT");
        });

        // initialize/update the list every time there is a change made to the habit events
        userCollectionReference.document(username).collection("Habits").document(habit.getId())
                .collection("Habit Events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                habit.clearHabitEvents();
                Map<String,Object> habitEventData;
                for(QueryDocumentSnapshot document:value) {
                    habitEventData = document.getData();
                    Log.d(TAG, document.getId() + " => " + habitEventData);
                    if (!habitEventData.isEmpty()) {
                        // every time we pull from Firestore, get the document ID data and associate it with the HabitEvent object

                        HabitEvent habitEvent = new HabitEvent(String.valueOf(habitEventData.get("completedOnTime")).equals("true"),
                                habitEventData.get("comment").toString());

                        Log.d(TAG,habitEventData.get("completedOnTime").toString());
                        Log.d(TAG,habitEventData.get("comment").toString());

                        habitEvent.setId(document.getId());
                        habit.addHabitEvent(habitEvent);
                    }
                }
                // update the data list
                habitEventDataList = habit.getHabitEvents();

                // notify the adapter
                habitEventAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void addHabitEvent(@Nullable HabitEvent habitEvent, Habit habit) {
        DocumentReference userDoc = userCollectionReference.document(username);
        assert habitEvent != null;
        userDoc.collection("Habits").document(habit.getId())
                .collection("Habit Events").add(habitEvent)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Habit event logged succesfully!", Toast.LENGTH_SHORT).show();
                    documentReference.update("id", documentReference.getId());
                })
                .addOnFailureListener(documentReference -> {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void deleteHabitEvent(HabitEvent habitEvent){
        DocumentReference userDoc = userCollectionReference.document(username)
                .collection("Habits").document(habit.getId());
        userDoc.collection("Habit Events").document(habitEvent.getId())
                .delete()
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Successfully deleted habit event", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(documentReference -> {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void updateHabitEvent(@Nullable HabitEvent newHabitEvent) {
        // get updated values
        assert newHabitEvent != null;
        String commentText = newHabitEvent.getComment();

        // update FireStore
        DocumentReference userDoc = userCollectionReference.document(username)
                .collection("Habits").document(habit.getId());
        userDoc.collection("Habit Events").document(newHabitEvent.getId())
                .update("comment", commentText);
        Log.d(TAG, newHabitEvent.getId());
    }

}