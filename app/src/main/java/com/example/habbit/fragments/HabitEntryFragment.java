package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.habbit.handlers.HabitInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HabitEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HabitEntryFragment extends DialogFragment {

    /* declare variables here so we have access throughout class */
    private EditText habitDateField;
    private Calendar myCalendar;

    public HabitEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habit Habit
     * @return A new instance of fragment AddHabitFragment.
     */
    public static HabitEntryFragment newInstance(Habit habit) {
        HabitEntryFragment fragment = new HabitEntryFragment();
        Bundle args = new Bundle();
        args.putSerializable("habit", habit);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This function formats the date for a Habit
     */
    /* determines how date should be shown in the EditText field */
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; // format of date desired
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        habitDateField.setText(sdf.format(myCalendar.getTime()));
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /* inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_entry, null);

        /* get habit details from args if there are any to get */
        Habit existingHabit = (Habit) (getArguments() != null ?
                getArguments().getSerializable("habit") : null);

        /* Initializes DatePicker dialog to enforce valid user input */
        myCalendar = Calendar.getInstance();
        habitDateField = view.findViewById(R.id.edit_habit_date);
        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        habitDateField.setOnClickListener(v -> new DatePickerDialog(getActivity(), date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        /* get EditTextFields for simple text entries */
        EditText habitTitleField = view.findViewById(R.id.edit_habit_title);
        EditText habitReasonField = view.findViewById(R.id.edit_habit_reason);

        /* get pickers for days of the week */
        ToggleButton tSun = (ToggleButton) view.findViewById(R.id.tSun);
        ToggleButton tMon = (ToggleButton) view.findViewById(R.id.tMon);
        ToggleButton tTue = (ToggleButton) view.findViewById(R.id.tTue);
        ToggleButton tWed = (ToggleButton) view.findViewById(R.id.tWed);
        ToggleButton tThu = (ToggleButton) view.findViewById(R.id.tThu);
        ToggleButton tFri = (ToggleButton) view.findViewById(R.id.tFri);
        ToggleButton tSat = (ToggleButton) view.findViewById(R.id.tSat);

        /* get publicity switch for toggling */
        Switch publicitySwitch = (Switch) view.findViewById(R.id.edit_publicity_switch);

        /* initialize add habit dialog */
        AlertDialog addDialog;
        if (existingHabit != null) {
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Edit Habit")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", null)
                    .show();
            habitTitleField.setText(existingHabit.getTitle());
            habitDateField.setText((existingHabit.getDate()));
            habitDateField.setClickable(false);
            habitDateField.setOnClickListener(null);
            habitReasonField.setText((existingHabit.getReason()));
            HashMap<String, Boolean> schedule = existingHabit.getSchedule();
            tSun.setChecked(schedule.get("Sun"));
            tMon.setChecked(schedule.get("Mon"));
            tTue.setChecked(schedule.get("Tue"));
            tWed.setChecked(schedule.get("Wed"));
            tThu.setChecked(schedule.get("Thu"));
            tFri.setChecked(schedule.get("Fri"));
            tSat.setChecked(schedule.get("Sat"));

            // set the correct value for the switch
            if (existingHabit.isPublic() != null){
                publicitySwitch.setChecked(existingHabit.isPublic());
                if (existingHabit.isPublic()){
                    publicitySwitch.setText("Public");
                } else {
                    publicitySwitch.setText("Private");
                }
            } else {
                publicitySwitch.setChecked(true);
                publicitySwitch.setText("Public");
            }

            // listen for check to change and if so change the text accordingly
            publicitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        publicitySwitch.setText("Public");
                    } else {
                        publicitySwitch.setText("Private");
                    }
                }
            });

        } else {
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Add Habit")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", null)
                    .show();


            // listen for check to change and if so change the text accordingly
            publicitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        publicitySwitch.setText("Public");
                    } else {
                        publicitySwitch.setText("Private");
                    }
                }
            });
        }

        /* set button behaviour separately in order to implement data validation */
        Button positiveButton = addDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view1 -> {
            String habitTitleText = habitTitleField.getText().toString();
            String habitDateText = habitDateField.getText().toString();
            String habitReasonText = habitReasonField.getText().toString();
            HashMap<String, Boolean> schedule = new HashMap<String, Boolean>();
            boolean habitIsPublic = publicitySwitch.isChecked();

            // check daypicker
            schedule.put("Sun", tSun.isChecked());
            schedule.put("Mon", tMon.isChecked());
            schedule.put("Tue", tTue.isChecked());
            schedule.put("Wed", tWed.isChecked());
            schedule.put("Thu", tThu.isChecked());
            schedule.put("Fri", tFri.isChecked());
            schedule.put("Sat", tSat.isChecked());

            boolean errorFlag = false;

            /* validate each field */
            if (habitTitleText.length() == 0 || habitTitleText.length() > 20) {
                habitTitleField.setError("Habit title cannot be empty or more than 20 characters");
                errorFlag = true;
            }

            if (habitReasonText.length() == 0 || habitReasonText.length() > 30) {
                habitReasonField.setError("Habit reason cannot be empty or more than 30 characters");
                errorFlag = true;
            }

            if (habitDateText.length() == 0 || habitDateText.equals("0")) {
                habitDateField.setError("Habit start date must be set");
                errorFlag = true;
            }

            if (!schedule.containsValue(Boolean.TRUE)) {
                TextView occursOnLabel = view.findViewById(R.id.occurs_on_label);
                occursOnLabel.setError("Habit schedule must be set");
                errorFlag = true;
            }

            // get handler to respond to habit interactions
            HabitInteractionHandler handler = new HabitInteractionHandler();

            /*
             if there are any errors, do not add the habit to the list yet
             and do not dismiss the dialog
            */
            if (errorFlag) {
                Toast.makeText(getActivity(), "Please fix errors", Toast.LENGTH_SHORT).show();
            } else {
                /* check if we are creating a new habit object or adjusting an existing one */
                if (existingHabit != null) {
                    existingHabit.setTitle(habitTitleText);
                    existingHabit.setDate(habitDateText);
                    existingHabit.setReason(habitReasonText);
                    existingHabit.setSchedule(schedule);
                    existingHabit.setPublicity(publicitySwitch.isChecked());
                    /* since this is an edit, we do not add a brand new medicine to the list */
                    handler.updateHabit(existingHabit);
                } else {
                    Habit newHabit = new Habit(habitTitleText, habitReasonText, habitDateText, schedule, habitIsPublic);
                    handler.addHabit(newHabit);
                }

                /* if everything is looking good, we can dismiss the dialog */
                addDialog.dismiss();
            }
        });

        return addDialog;
    }
}