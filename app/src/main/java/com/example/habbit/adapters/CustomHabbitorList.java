package com.example.habbit.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habbit.R;
import com.example.habbit.handlers.UserInteractionHandler;
import com.example.habbit.models.Habbitor;
import com.example.habbit.models.User;

import java.util.ArrayList;

/**
 * This class represents a list of Habbitors
 */
public class CustomHabbitorList extends ArrayAdapter<Habbitor> {

    /**
     * This variable is a {@link ArrayList} containing
     * {@link Habbitor} objects
     */
    private final ArrayList<Habbitor> habbitors;

    /**
     * This is a {@link Context}
     */
    private final Context context;

    /**
     * Constructor for CustomFriendList adapter
     *
     * @param context
     * @param habbitors
     */
    public CustomHabbitorList(Context context, ArrayList<Habbitor> habbitors) {
        super(context, 0, habbitors);
        this.habbitors = habbitors;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.friend_list_content, parent, false);
        }

        // get relevant habbitor
        Habbitor habbitor = habbitors.get(position);

        // get name of friend to display
        TextView friendNameView = view.findViewById(R.id.friend_name);
        friendNameView.setText(habbitor.getUsername());

        // linking button text to follow status of friend
        Button followButton = view.findViewById(R.id.follow_button);
        String followStatus = "";
        if (habbitor.isStranger()) {
            followStatus = "Follow";
        } else if (habbitor.isAcquaintance()) {
            followStatus = "Pending";
        } else if (habbitor.isFriend()) {
            followStatus = "Following";
        }
        followButton.setText(followStatus);

        followButton.setOnClickListener(view1 -> {
            // forward declaration of the new relationship we want our user to have to the given habbitor
            Integer newRelationship;

            if (habbitor.isStranger()) {
                // change follow status on button
                followButton.setText("Pending");

                // get new relationship type and store it
                newRelationship = Habbitor.relationshipTypes.get("Acquaintance");
                User.addRelationship(habbitor.getUserID(), newRelationship);
                habbitor.setRelationship(newRelationship);

                // push the changes to firestore
                UserInteractionHandler handler = new UserInteractionHandler();
                handler.updateUserRelationships(User.getUserID(), User.getRelationships());
                handler.addRequest(User.getUserID(), habbitor.getUserID());
            }
        });

        return view;
    }

}
