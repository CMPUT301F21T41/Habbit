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
        Intent intent = new Intent(this, MainActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable("Username",userName);
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
//        String message = "userName: "+userName+", password: "+password;
        Toast.makeText(this,userName,Toast.LENGTH_LONG).show();
        Toast.makeText(this,password,Toast.LENGTH_LONG).show();
        userCollectionReference.document(userName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()){
                    Toast.makeText(getApplicationContext(), "Found user!", Toast.LENGTH_LONG).show();
                    String validPass = task.getResult().get("Password").toString();
                    if (password.equals(validPass)){
                        Toast.makeText(getApplicationContext(),"user validated!",Toast.LENGTH_LONG).show();
                        startMainActivity(userName);
                    } else {
                        Toast.makeText(getApplicationContext(),"invalid password",Toast.LENGTH_LONG).show();
                        LogErrorFragment.newInstance("Invalid Password").show(getSupportFragmentManager(),"WRONG_PASS");
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(),"task success",Toast.LENGTH_LONG).show();
                    if (task.getResult().exists()){
                        Toast.makeText(getApplicationContext(),"User exists",Toast.LENGTH_LONG).show();
                        LogErrorFragment.newInstance("Username Already Exists").show(getSupportFragmentManager(),"USER_TAKEN");
                    } else {
                        if (password.equals(passConfirm)){
                            Toast.makeText(getApplicationContext(),"good to go",Toast.LENGTH_LONG).show();
                            addUser(email,userName,password);
                            Toast.makeText(getApplicationContext(),"added user",Toast.LENGTH_LONG).show();
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
