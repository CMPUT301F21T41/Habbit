package com.example.habbit;

import static android.content.ContentValues.TAG;
import static com.example.habbit.MainActivity.userCollectionReference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HabitEventsActivity extends AppCompatActivity implements HabitEventDetailsFragment.OnHabitEventDetailInteraction,
        HabitEventEntryFragment.OnHabitEventFragmentInteractionListener {

    CustomHabitEventList habitEventAdapter;
    ArrayList<HabitEvent> habitEventDataList;
    String userLoggedIn;
    Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_events);

        // initialize views
        ListView habitEventList = findViewById(R.id.habit_events_list);
        Button btnBackToHabits = findViewById(R.id.btn_back_to_habits);

        // get the habit containing desired habit events
        habit = (Habit) getIntent().getSerializableExtra("habit");
        habitEventDataList = habit.getHabitEvents();

        // set the adapter
        habitEventAdapter = new CustomHabitEventList(this, habitEventDataList);
        habitEventList.setAdapter(habitEventAdapter);


        //**GET USER LOGIN -- ADD LATER**
        userLoggedIn = "seanwruther9";

        /* instantiate a listener for habitList that will open a HabitDetailsFragment when a Habit is selected */
        habitEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HabitEvent viewHabitEvent = (HabitEvent) habitEventAdapter.getItem(i);
                HabitEventDetailsFragment.newInstance(viewHabitEvent).show(getSupportFragmentManager(),"VIEW_HABIT_EVENT");
            }
        });

        // initialize/update the list every time there is a change made to the habit events
        userCollectionReference.document(userLoggedIn).collection("Habits").document(habit.getId())
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

        // button onclick for going back to habits
        btnBackToHabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onHabitEventConfirmed(@Nullable HabitEvent habitEvent, Habit habit) {
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
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
    public void onDeleteHabitEventPressed(HabitEvent habitEvent){
        //Log.d("HabitEventsActivity", "habit id = " + habit.getId());
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn)
                .collection("Habits").document(habit.getId());
        //Log.d("HabitEventsActivity", "habitEvent id = " + habitEvent.getId());
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
    public void onEditHabitEventPressed(@Nullable HabitEvent newHabitEvent) {
        /* get updated values */
        assert newHabitEvent != null;
        String commentText = newHabitEvent.getComment();

        /* update FireStore */
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn)
                .collection("Habits").document(habit.getId());
        //Log.d("HabitEventsActivity", "habitEvent id = " + habitEvent.getId());
        userDoc.collection("Habit Events").document(newHabitEvent.getId())
                .update("comment", commentText);
        Log.d(TAG, newHabitEvent.getId());
    }
}