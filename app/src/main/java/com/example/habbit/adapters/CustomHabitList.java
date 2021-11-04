package com.example.habbit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habbit.activities.MainActivity;
import com.example.habbit.models.Habit;
import com.example.habbit.R;

import java.util.ArrayList;

/**
 * This class represents a list of habits
 */
public class CustomHabitList extends ArrayAdapter<Habit> {
    /**
     * This variable is an {@link ArrayList} containing
     * {@link Habit} objects
     */
    private ArrayList<Habit> habits;

    /**
     * This is a {@link Context}
     */
    private Context context;

    private OnCheckboxClickListener listener;

    public interface OnCheckboxClickListener {
        void onCheckboxClick(Habit habit, boolean isChecked);
    }

    /**
     *
     * @param context
     * @param habits
     */
    public CustomHabitList(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
        this.listener = (OnCheckboxClickListener) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_content, parent,false);
        }

        Habit habit = habits.get(position);

        // Linking xml text fields to text views in CustomHabitList.java
        TextView habitTitle = view.findViewById(R.id.habit_title);
        TextView habitDate = view.findViewById(R.id.habit_date);

        // setting checkbox value to value stored in the relevant habit object
        final CheckBox checkBox = view.findViewById(R.id.habit_checkbox);
        checkBox.setChecked(habit.isChecked());

        // setting checkbox behaviour
        checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            checkBox.setChecked(habit.isChecked());
            listener.onCheckboxClick(habit, isChecked);
        });

        // set texts
        habitTitle.setText(habit.getTitle());
        habitDate.setText(habit.getDate());

        return view;
    }
}