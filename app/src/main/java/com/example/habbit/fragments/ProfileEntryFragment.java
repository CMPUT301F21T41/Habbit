package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.habbit.R;
import com.example.habbit.handlers.UserInteractionHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileEntryFragment extends DialogFragment {

    FirebaseAuth userAuth;
    FirebaseUser user;

    public ProfileEntryFragment(){
        // required empty class constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param userData User data shown to user on profile edit, of type {@link Map}.
//     * @return A new instance of fragment {@link ProfileEntryFragment}.
//     */
//    public static ProfileEntryFragment newInstance(Map<String,Object> userData){
//        Bundle args = new Bundle();
//        args.putSerializable("USER", (Serializable) userData);
//
//        ProfileEntryFragment fragment = new ProfileEntryFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static ProfileEntryFragment newInstance(){
//        Bundle args = new Bundle();
//        args.putSerializable("USER",(Serializable) user);

        ProfileEntryFragment fragment = new ProfileEntryFragment();
//        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        /* Inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_entry,null);
        userAuth = FirebaseAuth.getInstance();

        /* connect Views to xml text fields */
        EditText viewEmail = view.findViewById(R.id.edit_profile_email);
        EditText viewName = view.findViewById(R.id.edit_profile_name);

        /* get the user data to display */
//        final FirebaseUser user = (FirebaseUser) (getArguments() != null ? getArguments().getSerializable("USER") : null);
        user = userAuth.getCurrentUser();

        /* set the text for the TextViews (null if habit is null) */
        String userName = "No Set Username";
        viewEmail.setText(user.getEmail());
        if (user.getDisplayName()!=null){
            userName = user.getDisplayName();
        }
        viewName.setText(userName);

        /* initialize handler to update profile information */
        UserInteractionHandler handler = new UserInteractionHandler();

        /* initialize the "Edit Profile" dialog */
        AlertDialog viewDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Edit Profile")
                .setNeutralButton("Close",null)
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    String name = viewName.getText().toString();
                    String email = viewEmail.getText().toString();
                    handler.updateUser(user, name, email);

                    viewEmail.setText(user.getEmail());
                    viewName.setText(user.getDisplayName());

                })
                .create();

        return viewDialog;
    }


}
