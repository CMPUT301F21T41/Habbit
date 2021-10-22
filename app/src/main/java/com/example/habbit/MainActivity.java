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

    //private final DocumentReference myDocRef = FirebaseFirestore.getInstance().document("users");
    //db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";
    final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
    User user = new User();
    String userLoggedIn;

    ListView habitList;
    ArrayAdapter<Habit> habitAdapter;
    List<Habit> habitDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FloatingActionButton addHabitButton = findViewById(R.id.add_habit_button);
        addHabitButton.setOnClickListener(view -> AddHabitFragment.newInstance(null)
                .show(getSupportFragmentManager(), "ADD_HABIT"));

        habitList = findViewById(R.id.habbitListView);
        habitDataList = user.getMyHabits();
        habitAdapter = new CustomHabitList(this, (ArrayList<Habit>) habitDataList);
        habitList.setAdapter(habitAdapter);

        /* instantiate a listener for habitList that will open a HabitDetailsFragment when a Habit is selected */
        habitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Habit viewHabit = habitAdapter.getItem(i);
                HabitDetailsFragment.newInstance(viewHabit).show(getSupportFragmentManager(),"VIEW_HABIT");
            }
        });

        //**GET USER LOGIN -- ADD LATER**
        userLoggedIn = "seanwruther9";

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                //final Map<String,Object> get = new HashMap<>();
                DocumentReference userDoc = collectionReference.document(userLoggedIn);
                userDoc.collection("Habits")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Map<String,String> data;
                                Object temp;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData().get(document.getId()));
                                        Log.d(TAG, "Message" + document.getData().get(document.getId()));
                                        data = (Map<String, String>) document.getData().get(document.getId());
                                        if(user.checkForHabits(document.getId()) == false){

                                           Habit habit = new Habit(data.get("title"),data.get("reason"),data.get("date"));
                                            //temp = (Habit) temp;
                                            //Collection<Habit> coll = data.values();

                                            //temp = data.get(document.getId());
                                            user.addHabit( habit) ;

                                        }
                                        habitAdapter.notifyDataSetChanged();
                                        Log.d(TAG,user.printHabits());
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

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
            collectionReference.document(userLoggedIn).collection("Habits").document(titleText).set(habitData);
        }


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                //final Map<String,Object> get = new HashMap<>();
                DocumentReference userDoc = collectionReference.document(userLoggedIn);
                userDoc.collection("Habits")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Map<String,String> data;
                                Object temp;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData().get(document.getId()));
                                        Log.d(TAG, "Message" + document.getData().get(document.getId()));
                                        data = (Map<String, String>) document.getData().get(document.getId());
                                        if(user.checkForHabits(document.getId()) == false){

                                            Habit habit = new Habit(data.get("title"),data.get("reason"),data.get("date"));
                                            //temp = (Habit) temp;
                                            //Collection<Habit> coll = data.values();

                                            //temp = data.get(document.getId());
                                            user.addHabit( habit) ;
                                        }
                                        habitAdapter.notifyDataSetChanged();
                                        Log.d(TAG,user.printHabits());
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }
        });
    }

    @Override
    public void onDeletePressed(Habit habit){

    }

    @Override
    public void onEditHabitPressed(Habit habit){

    }



}