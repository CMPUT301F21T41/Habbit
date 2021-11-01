package com.example.habbit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

public class HabitEventDetailsFragment extends DialogFragment {

    private OnHabitEventDetailInteraction listener;

        public interface OnHabitEventDetailInteraction {
            void onDeleteHabitEventPressed(HabitEvent habitEvent);
        }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHabitEventDetailInteraction){
            listener = (OnHabitEventDetailInteraction) context;
        } else {
            throw new RuntimeException(context.toString() +
            "must implement OnHabitEventClickListener");
        }
    }

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
    public static HabitEventDetailsFragment newInstance(HabitEvent habitEvent){
        Bundle args = new Bundle();
        args.putSerializable("viewEvent", (Serializable) habitEvent);

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
        TextView viewComment = view.findViewById(R.id.habit_event_comment_frag);
        TextView viewCOT = view.findViewById(R.id.completed_on_time_frag);
        TextView lblComment = view.findViewById(R.id.comment_lbl);
        TextView lblCOT = view.findViewById(R.id.COT_lbl);

        /* get the details of the habit, if there are any to get */
        final HabitEvent selectedHabitEvent = (HabitEvent) (getArguments() != null ?
                getArguments().getSerializable("viewEvent") : null);

        final Habit selectedHabit = (Habit) (getArguments() != null ?
                getArguments().getSerializable("viewHabit") : null);

        /* set the text for the TextViews (null if habit is null) */
        viewComment.setText(selectedHabitEvent != null ? selectedHabitEvent.getComment():null);
        viewCOT.setText(selectedHabitEvent != null ? String.valueOf(selectedHabitEvent.isCompletedOnTime()):null);


        /* initialize the "View HabitEvent" dialog */
        AlertDialog viewDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("View Habit Event")
                .setNeutralButton("Close",null)
                .setNegativeButton("Delete", (dialogInterface, i) ->
                        listener.onDeleteHabitEventPressed(selectedHabitEvent))
                .setPositiveButton("Edit",(dialogInterface, i) -> {
                    HabitEventEntryFragment.newInstance(selectedHabitEvent, selectedHabit).
                            show(requireActivity().getSupportFragmentManager(), "ADD_HABIT_EVENT");
                })
                .create();
        return viewDialog;
    }


}
