package com.example.habbit;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements HabitEntryFragment.OnHabitEntryFragmentInteractionListener,
        HabitDetailsFragment.OnHabitDetailInteraction,
        HabitEventEntryFragment.OnHabitEventFragmentInteractionListener,
        CustomHabitList.OnCheckboxClickListener {

    private static final String TAG = "MyActivity";
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

        habitList = findViewById(R.id.habitListView);
        habitDataList = user.getUserHabits();
        habitAdapter = new CustomHabitList(this, habitDataList);
        habitList.setAdapter(habitAdapter);

        //**GET USER LOGIN -- ADD LATER**
        userLoggedIn = "seanwruther9";

        /* add habit button */
        final FloatingActionButton addHabitButton = findViewById(R.id.add_habit_button);
        addHabitButton.setOnClickListener(view -> HabitEntryFragment.newInstance(null)
                .show(getSupportFragmentManager(), "ADD_HABIT"));

        /* instantiate a listener for habitList that will open a HabitDetailsFragment when a Habit is selected */
        habitList.setOnItemClickListener((adapterView, view, i, l) -> {
            Habit viewHabit = habitAdapter.getItem(i);
            HabitDetailsFragment.newInstance(viewHabit).show(getSupportFragmentManager(),"VIEW_HABIT");
        });

        /* refresh the listview every time we update Firestore */
        userCollectionReference.document(userLoggedIn).collection("Habits").addSnapshotListener((value, error) -> {
            user.clearHabits();
            Map<String,Object> habitData;
            assert value != null;
            for(QueryDocumentSnapshot document:value) {
                habitData = document.getData();
                Log.d(TAG, document.getId() + " => " + habitData);
                if (!habitData.isEmpty()) {
                    // every time we pull from Firestore, get the document ID data and associate it with the Habit object
                    Habit habit = new Habit(Objects.requireNonNull(habitData.get("title")).toString(), Objects.requireNonNull(habitData.get("reason")).toString(),
                            Objects.requireNonNull(habitData.get("date")).toString());
                    habit.setId(document.getId());
                    user.addHabit(habit);
                }
            }
            habitDataList=user.getUserHabits();
            habitAdapter.notifyDataSetChanged();
            Log.d(TAG,user.printHabits());
        });
    }

    @Override
    public void onAddHabitPressed(Habit habit) {
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

    @Override
    public void onDeleteHabitPressed(Habit habit){
        // TODO: Gonna need to figure out how to delete subcollections too, but not yet
        // doesn't really affect the app, just clutters up our firestore
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        userDoc.collection("Habits").document(habit.getId())
                .delete()
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Successfully deleted habit", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(documentReference ->
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show());
    }

    public void onEditHabitPressed(Habit newHabit){
        /* get the updated values */
        String titleText = newHabit.getTitle();
        String reasonText = newHabit.getReason();
        String dateText = newHabit.getDate();

        /* update the firestore */
        DocumentReference userDoc = userCollectionReference.document(userLoggedIn);
        userDoc.collection("Habits").document(newHabit.getId())
                .update("title", titleText,
                        "reason", reasonText,
                        "date", dateText);
        Log.d(TAG, newHabit.getId());
    }

    // TODO: is this the right way to interact with a fragment
    public void onCheckboxClick(Habit habit) {
        HabitEventEntryFragment.newInstance(null, habit).show(getSupportFragmentManager(),"HABIT_EVENT_ENTRY");
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
                .addOnFailureListener(documentReference -> Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT));
    }

    @Override
    public void onEditHabitEventPressed(@Nullable HabitEvent newHabitEvent) {

    }
}