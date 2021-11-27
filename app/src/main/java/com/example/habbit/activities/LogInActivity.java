package com.example.habbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habbit.R;
import com.example.habbit.fragments.LogErrorFragment;
import com.example.habbit.fragments.LogInFragment;
import com.example.habbit.fragments.RegisterFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class handles the actions available in the Log in screen
 */
public class LogInActivity extends AppCompatActivity implements LogInFragment.OnLogInFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {

    // TAG used for debugging
    private static final String TAG = "LogInActivity";

    final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    private FirebaseAuth userAuth;
    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        userAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        // check to see if someone is already signed in (currUser non-Null)
        FirebaseUser currUser = userAuth.getCurrentUser();
        if (currUser != null){
             startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("User",user);
        startActivity(intent);
    }

    private void addUser(String userID){
        Map<String,Object> userData = new HashMap<>();
        userData.put("User ID", userID);
        userCollectionReference.document(userID)
                .set(userData)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Succesfully added user!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Something went wrong!",
                        Toast.LENGTH_SHORT).show());
    }

    private void addUserName(FirebaseUser user, String userName) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"User profile updated");
                        }
                    }
                });
    }

    /**
     * checks whether or not the entered information is valid and if so continue to {@link MainActivity}
     * @param email
     * takes a username of type {@link String}
     * @param password
     * takes a password of type {@link String}
     */
    @Override
    public void OnLogInPressed(String email, String password){
        userAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign-in success, start MainActivity with new user information
                            Log.d(TAG,"createUserWithEmail:success");
                            FirebaseUser user = userAuth.getCurrentUser();
                            startMainActivity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errMessage = "Unknown Error";
                        if (e instanceof FirebaseAuthInvalidCredentialsException){
                            errMessage = "Invalid Password";
                        } else if (e instanceof  FirebaseAuthInvalidUserException) {
                            String errCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            if (errCode.equals("ERROR_USER_NOT_FOUND")){
                                errMessage = "User Not Found";
                            } else {
                                errMessage = e.getLocalizedMessage();
                            }
                        }
                        LogErrorFragment.newInstance(errMessage).show(getSupportFragmentManager(),"Log in failure");
                    }
                });
    }

    /**
     * Registers a new user with a provided email, userName, password, and confirmed password
     * @param email
     * Takes an email of type {@link String}
     * @param userName
     * Takes a userName of type {@link String}
     * @param password
     * Takes a password of type {@link String}
     * @param passConfirm
     * Takes a password Confirmation of type {@link String} that needs to match the password
     */
    @Override
    public void OnRegisterPressed(String email, String userName, String password, String passConfirm){
        if (password.equals(passConfirm)){
            userAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG,"createUserWithEmail:success");
                                FirebaseUser user = userAuth.getCurrentUser();

                                // create a new habit collection using the userID as the document name
                                addUser(user.getUid());
                                // attach the entered username to the created user as the "Display Name"
                                addUserName(user,userName);

                                startMainActivity();
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errMessage = e.getLocalizedMessage();
                            LogErrorFragment.newInstance(errMessage).show(getSupportFragmentManager(),"Registration failure");
                        }
                    });
        } else {
            LogErrorFragment.newInstance("Password Does Not Match").show(getSupportFragmentManager(),"Registration failure");
        }
    }

    /**
     *
     * @param ev
     * @return
     */
    // code taken from https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
    // closes the keyboard when you click outside of the edittext
    // TODO: see if we can adapt this to avoid plagiarism lol
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
