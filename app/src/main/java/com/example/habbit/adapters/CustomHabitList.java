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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habbit.R;
import com.example.habbit.activities.MainActivity;
import com.example.habbit.fragments.HabitDetailsFragment;
import com.example.habbit.models.Habit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * This class represents a list of habits
 */
public class CustomHabitList extends RecyclerView.Adapter<CustomHabitList.ViewHolder> {
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

    @NonNull
    @Override
    public CustomHabitList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.habit_list_content, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHabitList.ViewHolder holder, int position) {
        final Habit habit = habits.get(position);

        HashMap<String,Boolean> schedule = habit.getSchedule();
        Date date = habit.getDateObject();

        //Calculate Frequency
        int frequency = 0;

        for (Boolean value : schedule.values()) {
            if (value) {
                frequency += 1;
            }
        }
        System.out.println("Frequency is:" + frequency);

        // Extract day of the week
        // 1 is Sunday
        int day = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        day = calendar.get(Calendar.DAY_OF_WEEK);


        // Increment Progressbar
        int progress = 0;

//        if (day == 1) {
//            habit.setProgress(progress);
//        } else {
        progress = habit.getProgress()/frequency;
//        }
        ProgressBar progressBar = holder.progressBar;
        progressBar.setProgress(progress);
        System.out.println("The number is:" + progress);

        if (checkboxesVisible) {
            // set visibility of checkbox
            holder.checkBox.setVisibility(View.VISIBLE);

            // set visibility of progressbar
            holder.progressBar.setVisibility(View.GONE);

            // setting checkbox value to value stored in the relevant habit object
            holder.checkBox.setChecked(habit.isChecked());

            // setting checkbox behaviour
            holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                holder.checkBox.setChecked(habit.isChecked());
                listener.onCheckboxClick(habit, isChecked);
            });
        } else {
            // set visibility of checkbox
            holder.checkBox.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
        }


        // set texts
        holder.habitTitle.setText(habit.getTitle());
        holder.habitDate.setText(habit.getDate());

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HabitDetailsFragment.newInstance(habit).show(((AppCompatActivity)context).getSupportFragmentManager(),"VIEW_HABIT");
            }
        });

        // set a listener for habitList that will open a HabitDetailsFragment when a Habit is selected
        //habitRecyclerView.setOnItemClickListener((adapterView, view, i, l) -> {
        //    Habit viewHabit = relevantAdapter.getItem(i);
        //    HabitDetailsFragment.newInstance(viewHabit).show(getSupportFragmentManager(),"VIEW_HABIT");
        //});
    }

    @Override
    public int getItemCount() {
        return this.habits.size();
    }

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
        this.habits = habits;
        this.context = context;
        this.listener = (OnCheckboxClickListener) context;
        this.checkboxesVisible = checkboxesVisible;
    }




    public class ViewHolder  extends RecyclerView.ViewHolder{
        // Linking xml text fields to text views in CustomHabitList.java
        private TextView habitTitle;
        private TextView habitDate;
        private View parentView;

        // get reference to checkbox element
        private CheckBox checkBox;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View view){
            super(view);
            this.parentView = view;
            this.habitTitle = (TextView) view.findViewById(R.id.habit_title);
            this.habitDate = (TextView) view.findViewById(R.id.habit_date);
            this.checkBox = view.findViewById(R.id.habit_checkbox);
            this.progressBar= view.findViewById(R.id.habit_progressbar);
        }
    }
}