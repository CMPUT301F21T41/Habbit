package com.example.habbit.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habbit.R;
import com.example.habbit.models.Friend;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

/**
 * This class represents a list of friends
 */
public class CustomFriendList extends ArrayAdapter<Friend> {

    /**
     * This variable is a {@link ArrayList} containing
     * {@link Friend} objects
     */
    private final ArrayList<Friend> friends;

    /**
     * This is a {@link Context}
     */
    private final Context context;

    /**
     * Constructor for CustomFriendList adapter
     *
     * @param context
     * @param friends
     */
    public CustomFriendList(Context context, ArrayList<Friend> friends) {
        super(context, 0, friends);
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.friend_list_content, parent, false);
        }

        Friend friend = friends.get(position);

        // TODO: get name of friend to display

        // TODO: linking button text to follow status of friend

        // TODO: on click of the list item, take to friend's profile page

        return view;
    }

}
