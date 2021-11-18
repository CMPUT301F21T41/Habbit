package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.activities.MapActivity;
import com.example.habbit.activities.ProfileActivity;
import com.example.habbit.handlers.HabitEventInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;
import com.example.habbit.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HabitEventDetailsFragment extends DialogFragment {

    public HabitEventDetailsFragment(){
        // required empty class constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habitEvent The HabitEvent whose details we want to view, of type {@link Habit}.
     * @return A new instance of fragment {@link HabitEventDetailsFragment}.
     */
    public static HabitEventDetailsFragment newInstance(HabitEvent habitEvent, Habit habit){
        Bundle args = new Bundle();
        args.putSerializable("habitEvent", habitEvent);
        args.putSerializable("habit", habit);

        HabitEventDetailsFragment fragment = new HabitEventDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        /* Inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_event_details,null);

        /* connect Views to xml text fields */
        TextView commentField = view.findViewById(R.id.habit_event_comment_field);
        TextView completedOnTimeField = view.findViewById(R.id.completed_on_time_field);

        /* get the details of the habit, if there are any to get */
        final HabitEvent selectedHabitEvent = (HabitEvent) (getArguments() != null ?
                getArguments().getSerializable("habitEvent") : null);

        final Habit selectedHabit = (Habit) (getArguments() != null ?
                getArguments().getSerializable("habit") : null);

        HabitEventInteractionHandler handler = new HabitEventInteractionHandler(selectedHabit);

        /* set the text for the TextViews (null if habit is null) */
        commentField.setText(selectedHabitEvent != null ? selectedHabitEvent.getComment():null);
        completedOnTimeField.setText(selectedHabitEvent != null ? String.valueOf(selectedHabitEvent.isCompletedOnTime()):null);

        //navigate to map
        final FloatingActionButton mapViewButton = view.findViewById(R.id.mapview_button);
        mapViewButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MapActivity.class);
            //intent.putExtra("USER", username);
            startActivity(intent);
        });

        /* initialize the "View HabitEvent" dialog */
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setNeutralButton("Close",null)
                .setNegativeButton("Delete", (dialogInterface, i) ->
                {
                    assert selectedHabitEvent != null;
                    handler.deleteHabitEvent(selectedHabitEvent);
                })
                .setPositiveButton("Edit",(dialogInterface, i) -> HabitEventEntryFragment.newInstance(selectedHabitEvent, selectedHabit).
                        show(requireActivity().getSupportFragmentManager(), "ADD_HABIT_EVENT"))
                .create();
    }

}
