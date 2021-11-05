package com.example.habbit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileEntryFragment extends DialogFragment {

    private OnProfileEntryFragmentInteractionListener listener;

    public interface OnProfileEntryFragmentInteractionListener {
        void onEditProfilePressed(Map<String,Object> map);
    }

    /**
     *
     *
     * @param context get context of activity to attach, type {@link Context}
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileEntryFragmentInteractionListener){
            listener = (OnProfileEntryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    "must implement OnProfileEntryFragmentInteractionListener");
        }
    }

    public ProfileEntryFragment(){
        // required empty class constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userData User data shown to user on profile edit, of type {@link Map}.
     * @return A new instance of fragment {@link ProfileEntryFragment}.
     */
    public static ProfileEntryFragment newInstance(Map<String,Object> userData){
        Bundle args = new Bundle();
        args.putSerializable("USER", (Serializable) userData);

        ProfileEntryFragment fragment = new ProfileEntryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        /* Inflate layout for fragment */
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile_entry,null);

        /* connect Views to xml text fields */
        EditText viewEmail = view.findViewById(R.id.edit_profile_email);
        EditText viewName = view.findViewById(R.id.edit_profile_name);

        /* get the user data to display */
        final Map<String,Object> userData = (Map<String, Object>) (getArguments() != null ?
               getArguments().getSerializable("USER") : null);


        /* set the text for the TextViews (null if habit is null) */
        viewEmail.setText(userData.get("Email").toString());
        viewName.setText(userData.get("Name").toString());

        viewEmail.setHint("Email: " + userData.get("Email"));
        viewName.setHint("Name: " + userData.get("Name"));

        /* initialize the "Edit Profile" dialog */
        AlertDialog viewDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Edit Profile")
                .setNeutralButton("Close",null)
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    userData.put("Email",viewEmail.getText().toString());
                    userData.put("Name",viewName.getText().toString());
                    listener.onEditProfilePressed(userData);

                })
                .create();

        return viewDialog;
    }


}
