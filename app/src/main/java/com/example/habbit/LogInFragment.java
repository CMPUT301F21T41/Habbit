package com.example.habbit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass
 * Use the
 */
public class LogInFragment extends Fragment {

    private EditText passTextField;
    private EditText userTextField;
    private OnLogInFragmentInteractionListener listener;
    private Button changeFrag;
    private Button logButton;
    View view;

    public interface OnLogInFragmentInteractionListener {
        void OnLogInPressed(String userName, String password);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if (context instanceof OnLogInFragmentInteractionListener){
            listener = (OnLogInFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnLogInFragmentInteractionListener");
        }
    }

    public LogInFragment(){

    }

    public static LogInFragment newInstance(){
        LogInFragment fragment = new LogInFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_log_in,container,false);
        changeFrag = view.findViewById(R.id.log_change_button);
        logButton = view.findViewById(R.id.log_button);
        passTextField = view.findViewById(R.id.log_pass);
        userTextField = view.findViewById(R.id.log_username);

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userText = userTextField.getText().toString();
                String passText = passTextField.getText().toString();
                listener.OnLogInPressed(userText,passText);
            }
        });

        changeFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(view.getId(),RegisterFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
