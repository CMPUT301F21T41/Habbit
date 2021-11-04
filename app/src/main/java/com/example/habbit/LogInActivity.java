package com.example.habbit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity implements LogInFragment.OnLogInFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {

    private static final String TAG = "MyActivity";
    final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    private void startMainActivity(String userName){
        /* start the MainActivity class and pass it the username of the logged in user */
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Username",userName);
        startActivity(intent);
    }

    private void addUser(String email, String userName, String password){
        Map<String,Object> userData = new HashMap<>();
        userData.put("Username",userName);
        userData.put("Password",password);
        userData.put("Email",email);
        userCollectionReference.document(userName)
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Succesfully added user!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Something went wrong!",
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    public void OnLogInPressed(String userName, String password){
        userCollectionReference.document(userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()){
                    String validPass = task.getResult().get("Password").toString();
                    if (password.equals(validPass)){
                        startMainActivity(userName);
                    } else {
                        LogErrorFragment.newInstance("Invalid Password").show(getSupportFragmentManager(),"WRONG_PASS");
                    }
                } else {
                    LogErrorFragment.newInstance("User Not Found").show(getSupportFragmentManager(),"NO_USER");
                }
            }
        });
    }

    @Override
    public void OnRegisterPressed(String email, String userName, String password, String passConfirm){
        userCollectionReference.document(userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        LogErrorFragment.newInstance("Username Already Exists").show(getSupportFragmentManager(),"USER_TAKEN");
                    } else {
                        if (password.equals(passConfirm)){
                            addUser(email,userName,password);
                            startMainActivity(userName);
                        } else {
                            LogErrorFragment.newInstance("Password Does Not Match").show(getSupportFragmentManager(),"PASS_CONFIRM_FAIL");
                        }
                    }
                } else {
                    LogErrorFragment.newInstance(null).show(getSupportFragmentManager(),"GET_FAILED");
                }
            }
        });
    }

}
