package com.example.habbit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentSnapshot;
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

    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    static final CollectionReference nameCollectionReference = FirebaseFirestore.getInstance().collection("userNames");
    private FirebaseAuth userAuth;
    private boolean userNameExists = false;

    /**
     * A factory method that is run when the activity is first created
     * @param savedInstanceState The surrounding information pertinent to the created Activity
     *                           of type {@link Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        userAuth = FirebaseAuth.getInstance();
        userNameExists = false;
    }

    /**
     * A factory method that is run when the activity is first started
     */
    @Override
    public void onStart(){
        super.onStart();
        // check to see if someone is already signed in (currUser non-Null)
        FirebaseUser currUser = userAuth.getCurrentUser();
        if (currUser != null){
             startMainActivity();
        }
    }

    /**
     * Transitions the application into the {@link MainActivity}
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Adds the created Firebase User to the collection of users
     * @param userID The user id, generated with {@link FirebaseUser#getUid()}.
     *               Of type {@link String}
     */
    private void addUser(String userID, String username){
        Map<String,Object> userData = new HashMap<>();
        userData.put("User ID", userID);
        userCollectionReference.document(userID)
                .set(userData)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Succesfully added user!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Something went wrong!",
                        Toast.LENGTH_SHORT).show());

        Map<String,Object> userNames = new HashMap<>();
        userNames.put("User Name",username);
        nameCollectionReference.document(username)
                .set(userNames)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Succesfully added username!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Something went wrong!",
                        Toast.LENGTH_SHORT).show());
    }

    /**
     *
     * @param userName
     */
    private void addUserName(String userName) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"User profile updated");
                        }
                    }
                });
    }

    private void registerAccount(String email, String password, String userName){
        userAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"createUserWithEmail:success");
                            FirebaseUser user = userAuth.getCurrentUser();

                            // create a new habit collection using the userID as the document name
                            addUser(user.getUid(),userName);
                            // attach the entered username to the created user as the "Display Name"
                            addUserName(userName);

                            startMainActivity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errMessage = e.getLocalizedMessage();
                        if (e instanceof FirebaseAuthInvalidCredentialsException){
                            String errCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            if (errCode.equals("ERROR_INVALID_EMAIL")){
                                errMessage = "Invalid email address.";
                            }
                        }
                        LogErrorFragment.newInstance(errMessage)
                                .show(getSupportFragmentManager(),"Registration failure");
                    }
                });
    }

    private void signIn(String email, String password){
        userAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign-in success, start MainActivity with new user information
                            Log.d(TAG,"createUserWithEmail:success");
//                            FirebaseUser user = userAuth.getCurrentUser();
                            startMainActivity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errMessage = e.getLocalizedMessage();
                        if (e instanceof  FirebaseAuthInvalidUserException){
                            String errCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            if (errCode.equals("ERROR_USER_NOT_FOUND")){
                                errMessage = "User not found.";
                            }
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException){
                            String errCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            if (errCode.equals("ERROR_INVALID_EMAIL")){
                                errMessage = "User not found.";
                            } else if (errCode.equals("ERROR_WRONG_PASSWORD")){
                                errMessage = "Invalid Password.";
                            }
                        }
                        LogErrorFragment.newInstance(errMessage)
                                .show(getSupportFragmentManager(),"Log in failure");
                    }
                });
    }

    /**
     * checks whether or not the entered information is valid and if so continue to {@link MainActivity}
     * @param email
     * takes an email of type {@link String}
     * @param password
     * takes a password of type {@link String}
     */
    @Override
    public void OnLogInPressed(String email, String password){
        EditText emailText = findViewById(R.id.log_email);
        EditText passText = findViewById(R.id.log_pass);

        if (emailText.getText().toString().equals("") || passText.getText().toString().equals("")){
            LogErrorFragment.newInstance("Credentials fields cannot be empty.")
                    .show(getSupportFragmentManager(),"Log in failure");
        } else {
            signIn(email,password);
        }
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

        if(email.equals("") || userName.equals("") || password.equals("") || passConfirm.equals("")) {
            LogErrorFragment.newInstance("Credentials fields cannot be empty.")
                    .show(getSupportFragmentManager(),"Registration failure");
        } else if (!password.equals(passConfirm)){
            LogErrorFragment.newInstance("Passwords do not match.")
                    .show(getSupportFragmentManager(),"Registration failure");
        } else {
            nameCollectionReference.document(userName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()){
                                    Toast.makeText(getApplicationContext(),"Got in here",Toast.LENGTH_SHORT).show();
                                    LogErrorFragment.newInstance("User with this username already exists.")
                                            .show(getSupportFragmentManager(),"Registration failure");
                                } else {
                                    registerAccount(email,password,userName);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Hides the touch keyboard whenever the user clicks outside of an {@link android.widget.EditText} box
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
