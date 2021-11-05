package com.example.habbit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabitList;
import com.example.habbit.fragments.HabitDetailsFragment;
import com.example.habbit.fragments.HabitEntryFragment;
import com.example.habbit.fragments.HabitEventEntryFragment;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.example.habbit.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


/**
 * This class maintains a listview of Habits belonging to the class {@link Habit}
 */
public class MainActivity extends AppCompatActivity
        implements HabitEntryFragment.OnHabitEntryFragmentInteractionListener,
        HabitDetailsFragment.OnHabitDetailInteractionListener,
        HabitEventEntryFragment.OnHabitEventFragmentInteractionListener,
        CustomHabitList.OnCheckboxClickListener,
        Serializable {

    // TAG used for debugging
    private static final String TAG = "MyActivity";

    // references to entities used throughout the class
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    User user = new User();
    String userLoggedIn;
    ListView habitList;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get references to UI elements and attach custom adapter
        /* load in the username from the Activity Bundle parameter */
        // TODO: Implement (or remove?) a method for checking if b is null (no username given)
        Bundle b = getIntent().getExtras();
        if (b==null){
            userLoggedIn = "seanwruther9";
        } else {
            userLoggedIn = b.get("Username").toString();
        }

        habitList = findViewById(R.id.habitListView);
        habitDataList = user.getUserHabits();
        habitAdapter = new CustomHabitList(this, habitDataList);
        habitList.setAdapter(habitAdapter);

        /* add habit button */
        final FloatingActionButton addHabitButton = findViewById(R.id.add_habit_button);
        addHabitButton.setOnClickListener(view -> HabitEntryFragment.newInstance(null)
                .show(getSupportFragmentManager(), "ADD_HABIT"));

        user.setUsername(userLoggedIn);
        final FloatingActionButton profileNavButton = findViewById(R.id.profile_nav_button);
        profileNavButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ProfileActivity.class);
            //Bundle bundle = new Bundle();
            intent.putExtra("USER", userLoggedIn);
            startActivity(intent);
        });
            //.show(getSupportFragmentManager(), "PROFILE"));



        /* instantiate a listener for habitList that will open a HabitDetailsFragment when a Habit is selected */
        habitList.setOnItemClickListener((adapterView, view, i, l) -> {
            Habit viewHabit = habitAdapter.getItem(i);
            HabitDetailsFragment.newInstance(viewHabit).show(getSupportFragmentManager(),"VIEW_HABIT");
        });

        // refresh the listview every time we update Firestore
        userCollectionReference.document(userLoggedIn).collection("Habits").addSnapshotListener((value, error) -> {
            // we first clear all the habits we have currently stored for the user
            user.clearHabits();

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
                    user.addHabit(habit);
                }
            }

            // redraw the listview with the newly updated habits
            habitDataList=user.getUserHabits();
            habitAdapter.notifyDataSetChanged();
            Log.d(TAG,user.printHabits());
        });
    }


    /**
     * The following function is to implement {@link com.example.habbit.fragments.HabitEntryFragment.OnHabitEntryFragmentInteractionListener}
     */
    @Override
    public void addHabit(Habit habit) {
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        // adding a habit using an autogenerated ID rather than using titletext
        userDoc.collection("Habits")
                .add(habit)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("id", documentReference.getId());
                    Toast.makeText(this, "Succesfully added habit!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Something went wrong!",
                        Toast.LENGTH_SHORT).show());
    }

    /**
     * The following functions are to implement {@link HabitDetailsFragment.OnHabitDetailInteractionListener}
     */
    @Override
    public void deleteHabit(Habit habit){
        // TODO: Gonna need to figure out how to delete subcollections too, but not yet
        // doesn't really affect the app, just clutters up our firestore
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        userDoc.collection("Habits").document(habit.getId())
                .delete()
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Successfully deleted habit", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(documentReference ->
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show());
    }

    /**
     * The following function is to implement {@link com.example.habbit.fragments.HabitEntryFragment.OnHabitEntryFragmentInteractionListener}
     */
    @Override
    public void updateHabit(Habit newHabit){
        // get the updated values
        String titleText = newHabit.getTitle();
        String reasonText = newHabit.getReason();
        String dateText = newHabit.getDate();
        boolean isChecked = newHabit.isChecked();

        // update the firestore
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        userDoc.collection("Habits").document(newHabit.getId())
                .update("title", titleText,
                        "reason", reasonText,
                        "date", dateText,
                        "checked", isChecked);
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
            updateHabit(habit);
        }
    }


    /**
     * the following function is to implement {@link HabitEventEntryFragment.OnHabitEventFragmentInteractionListener}
     */
    @Override
    public void addHabitEvent(@Nullable HabitEvent habitEvent, Habit habit) {
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        assert habitEvent != null;

        // set the habit checked value to true since we have logged a habit event for the day
        habit.setChecked(true);
        updateHabit(habit);

        // update firestore with the new habit event
        userDoc.collection("Habits").document(habit.getId())
                .collection("Habit Events").add(habitEvent)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Habit event logged succesfully!", Toast.LENGTH_SHORT).show();
                    documentReference.update("id", documentReference.getId());
                })
                .addOnFailureListener(documentReference -> Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT));
    }

    /**
     *
     * the following function is to implement {@link HabitEventEntryFragment.OnHabitEventFragmentInteractionListener}
     */
    @Override
    public void updateHabitEvent(@Nullable HabitEvent newHabitEvent) {
        // empty update habit event method to implement habit event fragment interaction listener
    }
}