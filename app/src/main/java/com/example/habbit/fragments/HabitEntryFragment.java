package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.habbit.models.Habit;
import com.example.habbit.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HabitEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HabitEntryFragment extends DialogFragment {

    /* declare variables here so we have access throughout class */
    private EditText habitTitleField;
    private EditText habitDateField;
    private EditText habitReasonField;
    private OnHabitEntryFragmentInteractionListener listener;
    private Calendar myCalendar;

    public interface OnHabitEntryFragmentInteractionListener {
        void onAddHabitPressed(Habit habit);
        void onEditHabitPressed(Habit existingHabit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHabitEntryFragmentInteractionListener) {
            listener = (OnHabitEntryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHabitEntryFragmentInteractionListener");
        }
    }

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
        args.putSerializable("habit", (Serializable) habit);
        fragment.setArguments(args);
        return fragment;
    }

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
        habitTitleField = view.findViewById(R.id.edit_habit_title);
        habitReasonField = view.findViewById(R.id.edit_habit_reason);

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
            habitReasonField.setText((existingHabit.getReason()));
        } else {
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Add Habit")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", null)
                    .show();
        }

        /* set button behaviour separately in order to implement data validation */
        Button positiveButton = addDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view1 -> {
            String habitTitleText = habitTitleField.getText().toString();
            String habitDateText = habitDateField.getText().toString();
            String habitReasonText = habitReasonField.getText().toString();

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

            /*
             if there are any errors, do not add the habit to the list yet
             and do not dismiss the dialog
            */
            if (errorFlag) {
                Toast.makeText(getActivity(), "Please fix errors", Toast.LENGTH_SHORT).show();
            } else {
                /* check if we are creating a new medicine object or adjusting an existing one */
                if (existingHabit != null) {
                    existingHabit.setTitle(habitTitleText);
                    existingHabit.setDate(habitDateText);
                    existingHabit.setReason(habitReasonText);
                    /* since this is an edit, we do not add a brand new medicine to the list */
                    listener.onEditHabitPressed(existingHabit);
                } else {
                    Habit newHabit = new Habit(habitTitleText, habitReasonText, habitDateText);
                    listener.onAddHabitPressed(newHabit);
                }

                /* if everything is looking good, we can dismiss the dialog */
                addDialog.dismiss();
            }
        });

        return addDialog;
    }
}