package com.example.habbit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddHabitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddHabitFragment extends DialogFragment {

    /* declare variables here so we have access throughout class */
    private EditText habitTitleField;
    // TODO: Implement habit date field properly
    private EditText habitDateField;
    private EditText habitReasonField;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void onAddOkPressed(@Nullable Habit habit);
        void onEditHabitPressed(Habit ogHabit, Habit newHabit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public AddHabitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habit Habit
     * @return A new instance of fragment AddHabitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddHabitFragment newInstance(Habit habit) {
        AddHabitFragment fragment = new AddHabitFragment();
        Bundle args = new Bundle();
        args.putSerializable("habit", (Serializable) habit);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /* inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_habit, null);

        /* get habit details from args if there are any to get */
        Habit existingHabit = (Habit) (getArguments() != null ?
                getArguments().getSerializable("habit") : null);

        habitTitleField = view.findViewById(R.id.edit_habit_title);
        habitDateField = view.findViewById(R.id.edit_habit_date);
        habitReasonField = view.findViewById(R.id.edit_habit_reason);


        /* initialize add habit dialog */
        AlertDialog addDialog;
        if (existingHabit != null) {
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Edit Habit")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Edit", null)
                    .show();
            habitTitleField.setText(existingHabit.getTitle());
            habitDateField.setText((existingHabit.getDate()));
            habitReasonField.setText((existingHabit.getReason()));
        } else {
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Add Habit")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", null)
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

                habitTitleField.requestFocus();
                habitTitleField.setError("Habit title cannot be empty or more than 20 characters");
                errorFlag = true;
            }

            if (habitReasonText.length() == 0 || habitReasonText.length() > 30) {
                habitReasonField.requestFocus();
                habitReasonField.setError("Habit reason cannot be empty or more than 30 characters");
                errorFlag = true;
            }

            if (habitDateText.length() == 0 || habitDateText.equals("0")) {
                habitDateField.requestFocus();
                habitDateField.setError("Habit start date must be set");
                errorFlag = true;
            }

            /*
             if there are any errors, do not add the medicine to the list yet
             and do not dismiss the dialog
            */
            if (errorFlag) {
                Toast.makeText(getActivity(), "Please fix errors", Toast.LENGTH_SHORT).show();
            } else {
                /* check if we are creating a new medicine object or adjusting an existing one */
                if (existingHabit != null) {
                    Habit ogHabit = new Habit(existingHabit.getTitle()
                            , existingHabit.getReason(), existingHabit.getDate());
                    existingHabit.setTitle(habitTitleText);
                    existingHabit.setDate(habitDateText);
                    existingHabit.setReason(habitReasonText);
                    /* since this is an edit, we do not add a brand new medicine to the list */
                    listener.onEditHabitPressed(ogHabit, existingHabit);
                } else {
                    listener.onAddOkPressed(new Habit(
                            habitTitleText,
                            habitReasonText,
                            habitDateText
                    ));
                }

                /* if everything is looking good, we can dismiss the dialog */
                addDialog.dismiss();
            }
        });

        return addDialog;
    }
}