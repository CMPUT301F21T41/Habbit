package com.example.habbit;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AddHabitFragment.OnFragmentInteractionListener, HabitDetailsFragment.OnHabitClickListener {

    private static final String TAG = "MyActivity";
    final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
    User user = new User();
    String userLoggedIn;

    ListView habitList;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        habitList = findViewById(R.id.habbitListView);
        habitDataList = user.getMyHabits();
        habitAdapter = new CustomHabitList(this, habitDataList);
        habitList.setAdapter(habitAdapter);

        //**GET USER LOGIN -- ADD LATER**
        userLoggedIn = "seanwruther9";

        final FloatingActionButton addHabitButton = findViewById(R.id.add_habit_button);
        addHabitButton.setOnClickListener(view -> AddHabitFragment.newInstance(null)
                .show(getSupportFragmentManager(), "ADD_HABIT"));

        /* instantiate a listener for habitList that will open a HabitDetailsFragment when a Habit is selected */
        habitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Habit viewHabit = habitAdapter.getItem(i);
                HabitDetailsFragment.newInstance(viewHabit).show(getSupportFragmentManager(),"VIEW_HABIT");
            }
        });

        collectionReference.document(userLoggedIn).collection("Habits").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                user.clearHabits();
                Map<String,String> data;
                for(QueryDocumentSnapshot document:value){
                    Log.d(TAG, document.getId() + " => " + document.getData().get(document.getId()));
                    Log.d(TAG, "Message" + document.getData().get(document.getId()));
                    data = (Map<String, String>) document.getData().get(document.getId());
                    Habit habit = new Habit(data.get("title"),data.get("reason"),data.get("date"));
                    user.addHabit(habit);
                }
                habitDataList=user.getMyHabits();
                habitAdapter.notifyDataSetChanged();
                Log.d(TAG,user.printHabits());
            }
        });
    }

    @Override
    public void onAddOkPressed(@Nullable Habit habit) {

        if (habit == null) throw new AssertionError();
        String titleText = habit.getTitle();
        String reasonText = habit.getReason();
        String dateText = habit.getDate();

        HashMap<String, Object> habitData = new HashMap<>();

        if(!titleText.isEmpty() && !reasonText.isEmpty() && !dateText.isEmpty() ){
            Habit newHabit = new Habit(titleText,reasonText,dateText);
            habitData.put(titleText,newHabit);

            collectionReference.document(userLoggedIn).collection("Habits")
                    .document(titleText)
                    .set(habitData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    @Override
    public void onDeletePressed(Habit habit){

    }

    @Override
    public void onEditHabitPressed(Habit habit){

    }



}