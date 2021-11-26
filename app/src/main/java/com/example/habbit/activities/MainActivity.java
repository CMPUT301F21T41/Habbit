package com.example.habbit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.habbit.R;
import com.example.habbit.adapters.CustomHabitList;
import com.example.habbit.fragments.HabitDetailsFragment;
import com.example.habbit.fragments.HabitEntryFragment;
import com.example.habbit.fragments.HabitEventEntryFragment;
import com.example.habbit.handlers.HabitInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * This class maintains a listview of Habits belonging to the class {@link Habit}
 */
public class MainActivity extends AppCompatActivity
        implements CustomHabitList.OnCheckboxClickListener {

    // TAG used for debugging
    private static final String TAG = "MainActivity";

    // references to entities used throughout the class
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    String username;
    ListView habitListView;
    ArrayAdapter<Habit> todayHabitAdapter;
    ArrayAdapter<Habit> allHabitAdapter;
    ArrayAdapter<Habit> relevantAdapter;
    ArrayList<Habit> todayHabitList;
    ArrayList<Habit> allHabitList;
    HabitInteractionHandler habitHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // custom top toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_logout));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // load in the username from the Activity Bundle parameter
        Bundle b = getIntent().getExtras();
        if (b==null){
            username = User.getUsername();
        } else {
            username = b.get("Username").toString();
            User.setUsername(username);
        }

        // get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date currentDate = new Date();
        String dayOfTheWeek = sdf.format(currentDate);
        Log.d(TAG, dayOfTheWeek);

        // get references to UI elements and attach custom adapter
        habitListView = findViewById(R.id.today_habit_list_view);
        todayHabitList = new ArrayList<>();
        todayHabitAdapter = new CustomHabitList(this, todayHabitList, true);
        allHabitList = User.getUserHabits();
        allHabitAdapter = new CustomHabitList(this, allHabitList, false);
        habitHandler = new HabitInteractionHandler();
        relevantAdapter =  allHabitAdapter;

        // custom bottom navbar
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.profile:
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("USER", username);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        // set a spinner to determine type of habit list to display
        Spinner habitTypeSpinner =  findViewById(R.id.habit_type_selector);
        /* create an ArrayAdapter using string array and default spinner layout */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.habit_types, R.layout.custom_spinner);
        /* specify layout to use when the list of choices appears */
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /* connect adapter to spinner */
        habitTypeSpinner.setAdapter(spinnerAdapter);
        habitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    relevantAdapter = allHabitAdapter;
                } else if (i == 1) {
                    relevantAdapter = todayHabitAdapter;
                }
                habitListView.setAdapter(relevantAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // set a listener for addHabitButton that will open a habit entry fragment when clicked
        final FloatingActionButton addHabitButton = findViewById(R.id.add_habit_button);
        addHabitButton.setOnClickListener(view -> HabitEntryFragment.newInstance(null)
                .show(getSupportFragmentManager(), "ADD_HABIT"));

        // set a listener for habitList that will open a HabitDetailsFragment when a Habit is selected
        habitListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Habit viewHabit = relevantAdapter.getItem(i);
            HabitDetailsFragment.newInstance(viewHabit).show(getSupportFragmentManager(),"VIEW_HABIT");
        });

        // refresh the listview every time we update Firestore
        userCollectionReference.document(username).collection("Habits").addSnapshotListener((value, error) -> {
            // we first clear all the habits we have currently stored for the user
            User.clearHabits();
            todayHabitList.clear();

            // pull updated list of habits from firestore
            boolean publicity;
            Map<String,Object> habitData;
            assert value != null;
            for(QueryDocumentSnapshot document:value) {
                habitData = document.getData();
                if (!habitData.isEmpty()) {
                    if (habitData.get("public") == null){
                        publicity = true;
                    } else {
                        publicity = (boolean) habitData.get("public");
                    }
                    // every time we pull from Firestore, get the document ID data and associate it with the Habit object
                    Habit habit = new Habit(Objects.requireNonNull(habitData.get("title")).toString(),
                            Objects.requireNonNull(habitData.get("reason")).toString(),
                            Objects.requireNonNull(habitData.get("date")).toString(),
                            (HashMap<String, Boolean>) Objects.requireNonNull(habitData.get("schedule")),
                            publicity);
                    habit.setChecked((boolean) Objects.requireNonNull(habitData.get("checked")));
                    habit.setId(document.getId());
                    User.addHabit(habit);

                    // only add the habit to displayed habits if it is scheduled
                    if (habit.getSchedule().get(dayOfTheWeek.substring(0, 3)) && currentDate.compareTo(habit.getDateObject()) >= 0) {
                        Log.d(TAG, document.getId() + " => " + habitData);
                        todayHabitList.add(habit);
                    }
                }
            }

            // redraw the listview with the newly updated habits
            // allHabitList = User.getUserHabits();
            allHabitAdapter.notifyDataSetChanged();
            todayHabitAdapter.notifyDataSetChanged();
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