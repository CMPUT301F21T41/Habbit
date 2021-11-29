package com.example.habbit.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.habbit.R;
import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.adapters.CustomHabitList;
import com.example.habbit.handlers.HabitInteractionHandler;
import com.example.habbit.models.Habit;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Fragment} subclass for the Habit Details Overlay.
 * Use the {@link HabitDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HabitDetailsFragment extends DialogFragment {

    Habit selected;
    HabitInteractionHandler handler;

    ActivityResultLauncher<Intent> habitEventsActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    assert result.getData() != null;
                    int returnValue = result.getData().getIntExtra("progress", 0);
                    selected.setProgress(returnValue);
                    handler.updateHabit(selected);
                }
            });

    public HabitDetailsFragment(){
        // required empty class constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param habit The Habit whose details we want to view, of type {@link Habit}.
     * @return A new instance of fragment {@link HabitDetailsFragment}.
     */
    public static HabitDetailsFragment newInstance(Habit habit){
        Bundle args = new Bundle();
        args.putSerializable("habit", habit);

        HabitDetailsFragment fragment = new HabitDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        /* Inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_habit_details,null);

        /* connect Views to xml text fields */
        TextView viewTitle = view.findViewById(R.id.habit_title);
        TextView viewDate = view.findViewById(R.id.habit_date);
        TextView viewReason = view.findViewById(R.id.habit_reason);
        TextView btnHabitEvents = view.findViewById(R.id.view_habit_event_link);
        TextView txtPublicity = view.findViewById(R.id.habit_is_public);
        TextView viewSchedule = view.findViewById(R.id.habit_schedule);

        /* get the details of the habit, if there are any to get */
        selected = (Habit) (getArguments() != null ?
                getArguments().getSerializable("habit") : null);

        /* set the text for the TextViews (null if habit is null) */
        viewTitle.setText(selected != null ? selected.getTitle():null);
        Log.d("Habit Details Fragment", selected != null ? selected.getDate() : null);
        viewDate.setText(selected != null ? selected.getDate():null);
        viewReason.setText(selected != null ? selected.getReason():null);

        /* determine the string to display for schedule */
        String display_schedule = "";
        HashMap<String, Boolean> schedule = selected.getSchedule();
        for (Map.Entry<String, Boolean> day : schedule.entrySet()) {
            if (day.getValue()) {
                display_schedule += day.getKey() + ", ";
            }
        }
        viewSchedule.setText(display_schedule.substring(0, display_schedule.length() - 2));

        /* set the switch values */
        if (selected != null){
            if (selected.isPublic()){
                txtPublicity.setText("Public");
            } else {
                txtPublicity.setText("Private");
            }
        }

        // see habit events on click -> go to habit events screen
        btnHabitEvents.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(), HabitEventsActivity.class);
            // pass the selected habit on to the next activity
            i.putExtra("habit", selected);
            habitEventsActivity.launch(i);
        });

        // get handler for habit interactions
        handler = new HabitInteractionHandler();

        /* initialize the "View Habit" dialog */
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setNeutralButton("Close",null)
                .setNegativeButton("Delete", (dialogInterface, i) ->
                {
                    assert selected != null;
                    handler.deleteHabit(selected);
                })
                .setPositiveButton("Edit",(dialogInterface, i) -> HabitEntryFragment.newInstance(selected).
                        show(requireActivity().getSupportFragmentManager(),"ADD_HABIT"))
                .create();
    }

}
