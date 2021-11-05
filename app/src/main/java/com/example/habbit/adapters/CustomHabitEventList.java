package com.example.habbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habbit.models.HabitEvent;
import com.example.habbit.R;

import java.util.ArrayList;

/**
 * This class represents a list of habit events
 */

public class CustomHabitEventList extends ArrayAdapter<HabitEvent> {

    /**
     * This variable is an {@link ArrayList} containing
     * {@link HabitEvent} objects
     */
    private final ArrayList<HabitEvent> habitEvents;

    /**
     * This is a {@link Context}
     */
    private final Context context;

    /**
     *
     * @param context the context the custom list is launched in
     * @param habitEvents the list of habit events to display
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
        String commentTxt = habitEvent.getComment();
        if (commentTxt.equals("")){
            commentTxt = "No Comment.";
        }

        String completedOntTimeTxt;

        // TODO: take random string from a list of strings for completion/noncompletion messages
        if(habitEvent.isCompletedOnTime()){
            completedOntTimeTxt = "Completed on time :)";
        } else {
            completedOntTimeTxt = "Not completed on time :(";
        }
        habitEventComment.setText(commentTxt);
        habitEventCompletedOnTime.setText(completedOntTimeTxt);

        return view;
    }
}
