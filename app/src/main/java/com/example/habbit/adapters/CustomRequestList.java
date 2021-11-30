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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Adapter class to control behaviour of follow request items within a listview
 */
public class CustomRequestList extends ArrayAdapter<String> {
    /**
     * This variable is a {@link java.util.ArrayList} containing
     * {@link String} objects that describe a habbitor ID
     */
    private final ArrayList<String> requests;

    /**
     * This is a {@link Context}
     */
    private final Context context;

    /**
     * Constructor for our custom adapter
     * @param requests List of habbitor ids that are requesting
     * @param context App context
     */
    public CustomRequestList(Context context, ArrayList<String> requests) {
        super(context, 0, requests);
        this.requests = requests;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.request_list_content, parent, false);
        }

        String habbitorID = requests.get(position);

        // get handler to handle our firestore requests
        UserInteractionHandler handler = new UserInteractionHandler();

        // get name of habbitor to display
        TextView requestName = view.findViewById(R.id.request_name);
        handler.displayName(habbitorID, requestName);

        // set behaviour of accept button
        Button acceptButton = view.findViewById(R.id.accept_follow);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add a bunch of stuff
                handler.acceptRequest(User.getUserID(), habbitorID);
                User.removeRequest(habbitorID);
                handler.updateUserRequests(User.getUserID(), User.getRequests());
                notifyDataSetChanged();
            }
        });

        // set behaviour of deny button
        Button denyButton = view.findViewById(R.id.deny_follow);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.removeRequest(habbitorID);
                handler.updateUserRequests(User.getUserID(), User.getRequests());
                notifyDataSetChanged();
            }
        });


        return view;
    }
}
