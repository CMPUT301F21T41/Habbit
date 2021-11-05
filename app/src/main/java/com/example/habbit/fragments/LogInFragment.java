package com.example.habbit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.habbit.R;

/**
 * A simple {@link Fragment} subclass
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
        view = inflater.inflate(R.layout.fragment_log_in,container,false);
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

        /* switch to the RegisterFragment if the changeFrag button is pressed */
        changeFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment fragment = new RegisterFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });

        return view;
    }
}
