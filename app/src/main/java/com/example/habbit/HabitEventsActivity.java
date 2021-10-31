package com.example.habbit;

import static android.content.ContentValues.TAG;
import static com.example.habbit.MainActivity.userCollectionReference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HabitEventsActivity extends AppCompatActivity {

    CustomHabitEventList habitEventAdapter;
    ArrayList<HabitEvent> habitEventDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_events);

        // initialize views
        ListView habitEventList = findViewById(R.id.habit_events_list);
        Button btnBackToHabits = findViewById(R.id.btn_back_to_habits);

        // get the habit containing desired habit events
        Habit habit = (Habit) getIntent().getSerializableExtra("habit");
        habitEventDataList = habit.getHabitEvents();

        // set the adapter
        habitEventAdapter = new CustomHabitEventList(this, habitEventDataList);
        habitEventList.setAdapter(habitEventAdapter);


        //**GET USER LOGIN -- ADD LATER**
        String userLoggedIn = "seanwruther9";


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
}