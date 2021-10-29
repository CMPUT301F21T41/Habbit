package com.example.habbit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {

    EditText regEmail;
    EditText regUser;
    EditText regPass;
    EditText regPassConfirm;
    Button register;
    Button changeFrag;
    OnRegisterFragmentInteractionListener listener;
    View view;

    public interface OnRegisterFragmentInteractionListener {
        void OnRegisterPressed(String email, String userName, String password, String passConfirm);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if (context instanceof RegisterFragment.OnRegisterFragmentInteractionListener){
            listener = (RegisterFragment.OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterFragmentInteractionListener");
        }
    }

    public RegisterFragment(){}

    public static RegisterFragment newInstance(){
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_log_in,container, false);
        regEmail = view.findViewById(R.id.reg_email);
        regUser = view.findViewById(R.id.reg_username);
        regPass = view.findViewById(R.id.reg_pass);
        regPassConfirm = view.findViewById(R.id.reg_pass_confirm);

        register = view.findViewById(R.id.reg_button);
        changeFrag = view.findViewById(R.id.reg_change_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = regEmail.getText().toString();
                String userName = regUser.getText().toString();
                String password = regPass.getText().toString();
                String passConfirm = regPassConfirm.getText().toString();
                listener.OnRegisterPressed(email,userName,password,passConfirm);
            }
        });

        changeFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(view.getId(),LogInFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
