package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.habbit.R;
import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.activities.MapActivity;
import com.example.habbit.activities.ProfileActivity;
import com.example.habbit.handlers.HabitEventInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HabitEventEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HabitEventEntryFragment extends DialogFragment {

    HabitEvent existingHabitEvent;

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
        args.putSerializable("habitEvent", habitEvent);
        args.putSerializable("habit", habit);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /* inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_event_entry, null);

        Button addPhotoBtn = view.findViewById(R.id.add_photo_link);
        Button addLocationBtn = view.findViewById(R.id.add_location_link);

        /* get the habit event and habit details from args if exists */
        existingHabitEvent = (HabitEvent) (getArguments() !=null ?
                getArguments().getSerializable("habitEvent") : null);
        assert getArguments() != null;
        Habit habit = (Habit) getArguments().getSerializable("habit");

        EditText commentField = view.findViewById(R.id.edit_habit_event_comment);

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

        HabitEventInteractionHandler handler = new HabitEventInteractionHandler(habit);

        if (existingHabitEvent == null) {
            HabitEvent habitEvent = new HabitEvent("placeholder");
            handler.addHabitEvent(habitEvent);
            existingHabitEvent = habitEvent;
            Button negativeButton = addDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(view2 -> {
                handler.deleteHabitEvent(existingHabitEvent);
            });
        }

        addLocationBtn.setOnClickListener(view3 ->{
            Intent intent = new Intent(getContext(), MapActivity.class);
            intent.putExtra("justView", 0);
            intent.putExtra("habitEvent", existingHabitEvent);
            intent.putExtra("habit", habit);
            startActivity(intent);
        });

        addPhotoBtn.setOnClickListener(view2 -> {
            HabitEventPhotoFragment.newInstance(existingHabitEvent, habit)
                    .show(requireActivity().getSupportFragmentManager(), "ADD_PHOTO_EVENT");
        });

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
                existingHabitEvent.setComment(comment);
                handler.updateHabitEvent(existingHabitEvent);

                addDialog.dismiss();
            }
        });

        return addDialog;
    }
}