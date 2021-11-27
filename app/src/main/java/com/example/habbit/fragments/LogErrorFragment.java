package com.example.habbit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.habbit.R;

/**
 * A simple {@link DialogFragment} subclass
 */
public class LogErrorFragment extends DialogFragment {

    /**
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
    }

    /**
     * The required empty constructor
     */
    public LogErrorFragment(){}

    /**
     *
     * @param err
     * @return
     */
   public static LogErrorFragment newInstance(String err){
        LogErrorFragment fragment = new LogErrorFragment();
        Bundle args = new Bundle();
        args.putSerializable("Log error", err);
        fragment.setArguments(args);
        return fragment;
   }

    /**
     *
     * @param savedInstanceState
     * @return
     */
   @NonNull
   @Override
   public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_log_error,null);

        String errMessage = (String) (getArguments() != null ? getArguments().getSerializable("Log error") : "Unknown error");

       return new AlertDialog.Builder(getContext())
//               .setView(view)
               .setMessage(errMessage)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Fragment parentFrag = getParentFragmentManager().findFragmentById(R.id.fragment_container);
                       if (parentFrag instanceof LogInFragment){
                           ((LogInFragment) parentFrag).clearText();
                       } else if (parentFrag instanceof RegisterFragment){
                           ((RegisterFragment) parentFrag).clearText();
                       }
                   }
               })
               .show();
   }
}
