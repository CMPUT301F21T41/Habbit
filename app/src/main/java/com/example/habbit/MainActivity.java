package com.example.habbit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddHabitFragment.OnFragmentInteractionListener {

    // Declared variables
    ListView habitList;
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FloatingActionButton addMedicineButton = findViewById(R.id.add_habit_button);
        addMedicineButton.setOnClickListener(view -> AddHabitFragment.newInstance(null)
                .show(getSupportFragmentManager(), "ADD_HABIT"));
    }

    @Override
    public void onAddOkPressed(@Nullable Habit habit) {
        // TODO: add the habit to Firebase here
    }
}