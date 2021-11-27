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
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment
 */
public class LogInFragment extends Fragment {

    private EditText passTextField;
    private EditText userTextField;
    private OnLogInFragmentInteractionListener listener;
    View view;

    /**
     *
     */
    public interface OnLogInFragmentInteractionListener {
        void OnLogInPressed(String userName, String password);
    }

    /**
     *
     * @param context
     */
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

    /**
     * The required empty constructor
     */
    public LogInFragment(){}

    /**
     *
     * @return
     */
    public static LogInFragment newInstance(){
        return new LogInFragment();
    }

    /**
     * This function clears all the {@link EditText} boxes and clears the current cursor focus
     */
    public void clearText(){
        passTextField.setText("");
        userTextField.setText("");
        view.clearFocus();
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_log_in,container,false);
        Button changeFrag = view.findViewById(R.id.log_change_button);
        Button logButton = view.findViewById(R.id.log_button);
        passTextField = view.findViewById(R.id.log_pass);
        userTextField = view.findViewById(R.id.log_email);
        clearText();

        logButton.setOnClickListener(view -> {
            String userText = userTextField.getText().toString();
            String passText = passTextField.getText().toString();
            listener.OnLogInPressed(userText,passText);
        });

        /* switch to the RegisterFragment if the changeFrag button is pressed */
        changeFrag.setOnClickListener(view -> {
            RegisterFragment fragment = new RegisterFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        });

        return view;
    }
}
