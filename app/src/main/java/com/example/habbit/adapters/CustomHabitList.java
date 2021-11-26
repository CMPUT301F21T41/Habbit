package com.example.habbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habbit.R;
import com.example.habbit.models.Habit;

import java.util.ArrayList;

/**
 * This class represents a list of habits
 */
public class CustomHabitList extends ArrayAdapter<Habit> {
    /**
     * This variable is an {@link ArrayList} containing
     * {@link Habit} objects
     */
    private final ArrayList<Habit> habits;

    /**
     * This is a {@link Context}
     */
    private final Context context;

    /**
     * This is a {@link boolean}
     */
    private final boolean checkboxesVisible;

    /**
     * This is a {@link OnCheckboxClickListener}
     */
    private final OnCheckboxClickListener listener;

    public interface OnCheckboxClickListener {
        void onCheckboxClick(Habit habit, boolean isChecked);
    }

    /**
     *
     * @param context the context the custom habit list is called in
     * @param habits the list of habits to display
     * @param checkboxesVisible whether or not to display checkboxes
     */
    public CustomHabitList(Context context, ArrayList<Habit> habits, boolean checkboxesVisible) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
        this.listener = (OnCheckboxClickListener) context;
        this.checkboxesVisible = checkboxesVisible;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.habit_list_content, parent,false);
        }

        Habit habit = habits.get(position);

        // Linking xml text fields to text views in CustomHabitList.java
        TextView habitTitle = view.findViewById(R.id.habit_title);
        TextView habitDate = view.findViewById(R.id.habit_date);

        // get reference to checkbox element
        final CheckBox checkBox = view.findViewById(R.id.habit_checkbox);
        if (checkboxesVisible) {
            // set visibility of checkbox
            checkBox.setVisibility(View.VISIBLE);

            // setting checkbox value to value stored in the relevant habit object
            checkBox.setChecked(habit.isChecked());

            // setting checkbox behaviour
            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                checkBox.setChecked(habit.isChecked());
                listener.onCheckboxClick(habit, isChecked);
            });
        } else {
            // set visibility of checkbox
            checkBox.setVisibility(View.GONE);
        }


        // set texts
        habitTitle.setText(habit.getTitle());
        habitDate.setText(habit.getDate());

        return view;
    }
}