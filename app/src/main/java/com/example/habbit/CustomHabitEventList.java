package com.example.habbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This class represents a list of habit events
 */

public class CustomHabitEventList extends ArrayAdapter {

    /**
     * This variable is an {@link ArrayList} containing
     * {@link HabitEvent} objects
     */
    private ArrayList<HabitEvent> habitEvents;

    /**
     * This is a {@link Context}
     */
    private Context context;

    /**
     *
     * @param context
     * @param habitEvents
     */
    public CustomHabitEventList(Context context, ArrayList<HabitEvent> habitEvents) {
        super(context, 0, habitEvents);
        this.habitEvents = habitEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.habit_event_list_content, parent,false);
        }

        HabitEvent habitEvent = habitEvents.get(position);

        // Linking xml text fields to text views in CustomHabitList.java
        TextView habitEventComment = view.findViewById(R.id.habit_event_comment);
        TextView habitEventCompletedOnTime = view.findViewById(R.id.habit_event_completed_on_time);

        // Set texts
        String commentTxt = "Comments: " + habitEvent.getComment();
        String completedOntTimeTxt = "Completed on time: " + String.valueOf(habitEvent.isCompletedOnTime());
        habitEventComment.setText(commentTxt);
        habitEventCompletedOnTime.setText(completedOntTimeTxt);

        return view;
    }
}
