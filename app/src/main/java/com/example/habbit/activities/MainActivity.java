package com.example.habbit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabitList;
import com.example.habbit.fragments.HabitDetailsFragment;
import com.example.habbit.fragments.HabitEntryFragment;
import com.example.habbit.fragments.HabitEventEntryFragment;
import com.example.habbit.handlers.HabitInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


/**
 * This class maintains a listview of Habits belonging to the class {@link Habit}
 */
public class MainActivity extends AppCompatActivity
        implements CustomHabitList.OnCheckboxClickListener {

    // TAG used for debugging
    private static final String TAG = "MyActivity";

    // references to entities used throughout the class
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    String username;
    ListView habitList;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitDataList;
    HabitInteractionHandler habitHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load in the username from the Activity Bundle parameter
        Bundle b = getIntent().getExtras();
        if (b==null){
            username = User.getUsername();
        } else {
            username = b.get("Username").toString();
            User.setUsername(username);
        }

        // get references to UI elements and attach custom adapter
        habitList = findViewById(R.id.habitListView);
        habitDataList = User.getUserHabits();
        habitAdapter = new CustomHabitList(this, habitDataList);
        habitList.setAdapter(habitAdapter);
        habitHandler = new HabitInteractionHandler();

        // set a listener for addHabitButton that will open a habit entry fragment when clicked
        final FloatingActionButton addHabitButton = findViewById(R.id.add_habit_button);
        addHabitButton.setOnClickListener(view -> HabitEntryFragment.newInstance(null)
                .show(getSupportFragmentManager(), "ADD_HABIT"));

        // set a listener for profileNavButton that will open the profile page when clicked
        final FloatingActionButton profileNavButton = findViewById(R.id.profile_nav_button);
        profileNavButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("USER", username);
            startActivity(intent);
        });

        // set a listener for habitList that will open a HabitDetailsFragment when a Habit is selected
        habitList.setOnItemClickListener((adapterView, view, i, l) -> {
            Habit viewHabit = habitAdapter.getItem(i);
            HabitDetailsFragment.newInstance(viewHabit).show(getSupportFragmentManager(),"VIEW_HABIT");
        });

        // refresh the listview every time we update Firestore
        userCollectionReference.document(username).collection("Habits").addSnapshotListener((value, error) -> {
            // we first clear all the habits we have currently stored for the user
            User.clearHabits();

            // pull updated list of habits from firestore
            Map<String,Object> habitData;
            assert value != null;
            for(QueryDocumentSnapshot document:value) {
                habitData = document.getData();
                Log.d(TAG, document.getId() + " => " + habitData);
                if (!habitData.isEmpty()) {

                    // every time we pull from Firestore, get the document ID data and associate it with the Habit object
                    Habit habit = new Habit(Objects.requireNonNull(habitData.get("title")).toString(), Objects.requireNonNull(habitData.get("reason")).toString(),
                            Objects.requireNonNull(habitData.get("date")).toString());
                    habit.setChecked((boolean) Objects.requireNonNull(habitData.get("checked")));
                    habit.setId(document.getId());
                    User.addHabit(habit);
                }
            }

            // redraw the listview with the newly updated habits
            habitDataList=User.getUserHabits();
            habitAdapter.notifyDataSetChanged();
            Log.d(TAG,User.printHabits());
        });
    }

    /**
     * The following function is to implement {@link CustomHabitList.OnCheckboxClickListener}
     */
    @Override
    public void onCheckboxClick(Habit habit, boolean isChecked) {
        Log.d(TAG, "i was clicked!");
        if (isChecked && !habit.isChecked()) {
            // only launch a new habit event entry fragment if the checkbox was clicked to true and
            // the currently stored habit checked value is false
            HabitEventEntryFragment.newInstance(null, habit).show(getSupportFragmentManager(),"HABIT_EVENT_ENTRY");
        } else if (!isChecked) {
            // if the habit was unchecked, set its value to false
            habit.setChecked(false);
            habitHandler.updateHabit(habit);
        }
    }

}