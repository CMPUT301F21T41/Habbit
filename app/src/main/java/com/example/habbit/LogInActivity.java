package com.example.habbit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity implements LogInFragment.OnLogInFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {

    private static final String TAG = "MyActivity";
    final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    EditText usernameField;
    EditText passwordField;
    EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    private void startMainActivity(String userName){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Username",userName);
        startActivity(intent);
    }

    @Override
    public void OnLogInPressed(String userName, String password){
        userCollectionReference.document(userName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        String validPass = (String) documentSnapshot.get("Password");
                        if (password.equals(validPass)){
                            startMainActivity(userName);
                        } else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void OnRegisterPressed(String email, String userName, String password, String passConfirm){

    }

}
