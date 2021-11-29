package com.example.habbit.adapters;


import android.content.Context;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * This class represents a list of friends
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

        Habbitor habbitor = habbitors.get(position);

        // get name of friend to display
        TextView friendNameView = view.findViewById(R.id.friend_name);
        friendNameView.setText(habbitor.getUsername());

        // linking button text to follow status of friend
        Button followButton = view.findViewById(R.id.follow_button);
        String followStatus = "Follow";
        if (habbitor.isStranger()) {
            followStatus = "Follow";
        } else if (habbitor.isAcquaintance()) {
            followStatus = "Pending";
        } else if (habbitor.isFriend()) {
            followStatus = "Following";
        }
        followButton.setText(followStatus);

        followButton.setOnClickListener(view1 -> {
            Integer newRelationship;

            if (habbitor.isStranger()) {
                followButton.setText("Pending");
                newRelationship = Habbitor.relationshipTypes.get("Acquaintance");
                User.addRelationship(habbitor.getUserID(), newRelationship);
                habbitor.setRelationship(newRelationship);
                UserInteractionHandler handler = new UserInteractionHandler();
                Log.d("CustomFriendList:", habbitor.getUserID());
                Log.d("CustomFriendList:", String.valueOf(User.getRelationships().get(habbitor.getUserID())));
                handler.updateUserRelationships(User.getUserID(), User.getRelationships());
            }
        });

        return view;
    }

}
