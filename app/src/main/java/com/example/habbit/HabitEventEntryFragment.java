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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HabitEventEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HabitEventEntryFragment extends DialogFragment {

    /* declare variables here so we have access throughout class */
    private EditText commentField;
    private OnHabitEventFragmentInteractionListener listener;

    // TODO: can we consolidate all of the interaction listeners?
    public interface OnHabitEventFragmentInteractionListener {
        void onHabitEventConfirmed(@Nullable HabitEvent habitEvent, Habit habit);
        void onEditHabitEventPressed(@Nullable HabitEvent habitEvent, Habit habit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHabitEventFragmentInteractionListener) {
            listener = (OnHabitEventFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHabitEventFragmentInteractionListener");
        }
    }

    public HabitEventEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habitEvent HabitEvent
     * @return A new instance of fragment HabitEventEntryFragment.
     */
    public static HabitEventEntryFragment newInstance(HabitEvent habitEvent, Habit habit) {
        HabitEventEntryFragment fragment = new HabitEventEntryFragment();
        Bundle args = new Bundle();
        args.putSerializable("habitEvent", (Serializable) habitEvent);
        args.putSerializable("habit", (Serializable) habit);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /* inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_event_entry, null);

        /* get the habit event and habit details from args if exists */
        HabitEvent existingHabitEvent = (HabitEvent) (getArguments() !=null ?
                getArguments().getSerializable("habitEvent") : null);

        Habit existingHabit = (Habit) (getArguments() != null?
                getArguments().getSerializable("habit") : null);

        commentField = view.findViewById(R.id.edit_habit_event_comment);


        /* initialize add habit event dialog */
        AlertDialog addDialog;

        if (existingHabitEvent != null) {
            System.out.println("HEY WE MADE IT HERE");
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Edit Habit Event")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", null)
                    .show();
            commentField.setText(existingHabitEvent.getComment());
        } else {
            addDialog = new AlertDialog.Builder(getContext())
                    .setView(view)
                    .setTitle("Log Habit Event")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", null)
                    .show();
        }

        // when a user confirms adding a habit event, we should add it to the list of habit events
        // associated with that Habit
        Button positiveButton = addDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view1 -> {
            String comment = commentField.getText().toString();

            boolean errorFlag = false;

            /* validate each field (photo and geolocation too once we get that working */

            if (comment.length() > 20) {
                commentField.setError("Comment cannot be greater than 20 characters");
                errorFlag = true;
            }

            /* If there are any errors, do not add the habit to the lis yet and do not dismiss dialog */
            if (errorFlag) {
                Toast.makeText(getActivity(), "Please fix errors", Toast.LENGTH_SHORT).show();
            } else {
                /* part where either create a new habit event OR adjusting an existing one */

                if (existingHabitEvent != null) {
                    existingHabitEvent.setComment(comment);
                    listener.onEditHabitEventPressed(existingHabitEvent, existingHabit);
                } else {
                    HabitEvent habitEvent = new HabitEvent(comment);
                    /* get habit associated if there are any to get */
                    // TODO: should we change this to be similar to what we have in HabitEntryFragment
                    assert getArguments() != null;
                    Habit habit = (Habit) getArguments().getSerializable("habit");
                    listener.onHabitEventConfirmed(habitEvent, habit);
                }

                addDialog.dismiss();
            }
        });

        return addDialog;
    }
}