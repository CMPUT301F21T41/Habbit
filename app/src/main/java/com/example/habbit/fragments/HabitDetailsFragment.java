package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.habbit.handlers.HabitInteractionHandler;
import com.example.habbit.models.Habit;
import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.R;
import com.example.habbit.models.User;

/**
 * A {@link Fragment} subclass for the Habit Details Overlay.
 * Use the {@link HabitDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HabitDetailsFragment extends DialogFragment {

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
        HabitInteractionHandler listener = new HabitInteractionHandler();

        /* Inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_view_habit,null);

        /* connect Views to xml text fields */
        TextView viewTitle = view.findViewById(R.id.habit_title);
        TextView viewDate = view.findViewById(R.id.habit_date);
        TextView viewReason = view.findViewById(R.id.habit_reason);
        Button btnHabitEvents = view.findViewById(R.id.btn_see_habit_events);

        /* get the habit of the details, if there are any to get */
        final Habit selected = (Habit) (getArguments() != null ?
                getArguments().getSerializable("habit") : null);

        // get the username of the user we are looking for
        String username = User.getUsername();

        /* set the text for the TextViews (null if habit is null) */
        viewTitle.setText(selected != null ? selected.getTitle():null);
        viewDate.setText(selected != null ? selected.getDate():null);
        viewReason.setText(selected != null ? selected.getReason():null);

        // see habit events on click -> go to habit events screen
        btnHabitEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(), HabitEventsActivity.class);
                // make the selected habit available in the next activity
                i.putExtra("habit", selected);
                startActivity(i);
            }
        });

        /* initialize the "View Habit" dialog */
        AlertDialog viewDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("View Habit")
                .setNeutralButton("Close",null)
                .setNegativeButton("Delete", (dialogInterface, i) ->
                        listener.deleteHabit(selected))
                .setPositiveButton("Edit",(dialogInterface, i) -> {
                    HabitEntryFragment.newInstance(selected).
                            show(requireActivity().getSupportFragmentManager(),"ADD_HABIT");
                })
                .create();
        return viewDialog;
    }

}
