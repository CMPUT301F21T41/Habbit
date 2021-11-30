package com.example.habbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habbit.R;
import com.example.habbit.models.Habit;

import java.util.ArrayList;

/**
 * This class represents a list of habits
 */
public class CustomHabitFriendList extends ArrayAdapter<Habit> {
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
     *
     * @param context the context the custom habit list is called in
     * @param habits the list of habits to display
     */
    public CustomHabitFriendList(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.habit_list_content, parent,false);
        }

        // remove checkboxes
        view.findViewById(R.id.habit_checkbox).setVisibility(View.GONE);


        Habit habit = habits.get(position);

        // set progress bar

        //Calculate Frequency
        int frequency = 0;

        for (Boolean value : habit.getSchedule().values()) {
            if (value) {
                frequency += 1;
            }
        }

        ProgressBar progressBar = view.findViewById(R.id.habit_progressbar);
        progressBar.setProgress(habit.getProgress()*100/frequency);

        // Linking xml text fields to text views in CustomHabitList.java
        TextView habitTitle = view.findViewById(R.id.habit_title);
        TextView habitDate = view.findViewById(R.id.habit_date);

        // set texts
        habitTitle.setText(habit.getTitle());
        habitDate.setText(habit.getDate());

        return view;
    }
}
