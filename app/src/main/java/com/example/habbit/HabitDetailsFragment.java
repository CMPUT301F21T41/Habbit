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
import android.widget.TextView;

import java.io.Serializable;

/**
 * A {@link Fragment} subclass for the Habit Details Overlay.
 * Use the {@link HabitDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HabitDetailsFragment extends DialogFragment {

    private OnHabitClickListener listener;

    public interface OnHabitClickListener {
        void onDeletePressed(Habit habit);
        //void onEditHabitPressed(Habit habit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHabitClickListener) {
            listener = (OnHabitClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHabitClickListener");
        }
    }

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
        args.putSerializable("view", (Serializable) habit);

        HabitDetailsFragment fragment = new HabitDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        /* Inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_view_habit,null);

        /* connect TextViews to xml text fields */
        TextView viewTitle = view.findViewById(R.id.habit_title);
        TextView viewDate = view.findViewById(R.id.habit_date);
        TextView viewReason = view.findViewById(R.id.habit_reason);

        /* get the habit of the details, if there are any to get */
        final Habit selected = (Habit) (getArguments() != null ?
                getArguments().getSerializable("view") : null);

        /* set the text for the TextViews (null if habit is null) */
        viewTitle.setText(selected != null ? selected.getTitle():null);
        viewDate.setText(selected != null ? selected.getDate():null);
        viewReason.setText(selected != null ? selected.getReason():null);

        /* initialize the "View Habit" dialog */
        AlertDialog viewDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("View Habit")
                .setNeutralButton("Close",null)
                .setNegativeButton("Delete", (dialogInterface, i) ->
                        listener.onDeletePressed(selected))
                .setPositiveButton("Edit",(dialogInterface, i) -> {
                    AddHabitFragment.newInstance(selected).
                            show(requireActivity().getSupportFragmentManager(),"ADD_HABIT");
                    //listener.onEditHabitPressed(selected);
                })
                .create();

        return viewDialog;
    }

}
