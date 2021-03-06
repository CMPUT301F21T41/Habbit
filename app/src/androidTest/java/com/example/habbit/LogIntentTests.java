package com.example.habbit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.activities.LogInActivity;
import com.example.habbit.activities.MainActivity;
import com.example.habbit.activities.ProfileActivity;
import com.example.habbit.fragments.LogInFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for LogInActivity. All UI tests written here, using Robotium
 */
public class LogIntentTests {
    private Solo solo;
    static final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");
    private static final String TAG = "LogIntents";
    // the user credentials of our pre-made dummy account
    private String defaultUid = "EAYzO736OSbrYTszyirwV5Qil1q1";
    private String defaultEmail = "default@default.com";
    private String defaultPass = "defaultPass";
    private String defaultName = "Default User";

    @Rule
    public ActivityTestRule<LogInActivity> rule = new ActivityTestRule<>(LogInActivity.class, true, true);

    /**
     * Runs before all tests and creates instance of solo
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // ensure that there is no user signed in before testing began
        while(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Log.i(TAG,FirebaseAuth.getInstance().getCurrentUser().toString());
            FirebaseAuth.getInstance().signOut();
        }

        // asserts that the current activity is LogInActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity",LogInActivity.class);

        // asserts that there is no user signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertEquals(null,user);

        // asserts that the LogInFragment is opened (with the title text 'LOGIN')
        assertTrue(solo.searchText("LOGIN"));
    }

    /**
     * Get activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void logInEmptyTest(){
        // click on the log in button without entering all credentials
        View logButton = solo.getView(R.id.log_button);
        solo.clickOnView(logButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("Credentials fields cannot be empty.",
                1,10000));
        solo.clickOnButton("Ok");

        // click on the log in button without entering a password
        solo.enterText((EditText) solo.getView(R.id.log_email),defaultEmail);
        solo.clickOnView(logButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("Credentials fields cannot be empty.",
                1,10000));
        solo.clickOnButton("Ok");

        // click on the log in button without entering an email
        solo.enterText((EditText) solo.getView(R.id.log_pass),defaultPass);
        solo.clickOnView(logButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("Credentials fields cannot be empty.",
                1,10000));
        solo.clickOnButton("Ok");
    }

    /**
     * Testing a successful log in
     */
    @Test
    public void logInSuccessTest(){
        solo.enterText((EditText) solo.getView(R.id.log_email),defaultEmail);
        solo.enterText((EditText) solo.getView(R.id.log_pass),defaultPass);

        View logInButton = solo.getView(R.id.log_button);
        solo.clickOnView(logInButton);

        // asserts that the MainActivity is now open
        solo.assertCurrentActivity("Wrong Activity",MainActivity.class);

        // asserts that the current user logged in is the default user account
        String currID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        assertEquals(defaultUid,currID);
    }

    /**
     * Testing all log in errors / failures
     */
    @Test
    public void logInErrorTest(){
        // ERROR 1: correct user email, incorrect password
        solo.enterText((EditText) solo.getView(R.id.log_email),defaultEmail);
        solo.enterText((EditText) solo.getView(R.id.log_pass),"incorrectPass");

        View logInButton = solo.getView(R.id.log_button);
        solo.clickOnView(logInButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("Invalid Password.",1,10000));
        solo.clickOnButton("Ok");

        // ERROR 2: User account is not found
        // i.e., invalid email input (xxx@xxx.xx not followed) or
        // valid email not associated with any user

        solo.clearEditText((EditText) solo.getView(R.id.log_email));
        solo.clearEditText((EditText) solo.getView(R.id.log_pass));

        solo.enterText((EditText) solo.getView(R.id.log_email),"defaultEmail");
        solo.enterText((EditText) solo.getView(R.id.log_pass),"incorrectPass");

        solo.clickOnView(logInButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("User not found.",1,10000));
        solo.clickOnButton("Ok");
    }

    @Test
    public void switchFragTest(){
        View logChangeButton = solo.getView(R.id.log_change_button);
        solo.clickOnView(logChangeButton);

        // assert that the RegisterFragment is open
        assertTrue(solo.waitForView(R.id.reg_email,1,5000));

        View regChangeButton = solo.getView(R.id.reg_change_button);
        solo.clickOnView(regChangeButton);

        // assert that the LogInFragment is open
        assertTrue(solo.waitForView(R.id.log_email,1,5000));
    }

    @Test
    public void registerEmptyTest(){
        View logChangeButton = solo.getView(R.id.log_change_button);
        solo.clickOnView(logChangeButton);

        View registerButton = solo.getView(R.id.reg_button);
        solo.clickOnView(registerButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("Credentials fields cannot be empty.",
                1,10000));
        solo.clickOnButton("Ok");
    }

    @Test
    public void registerSuccessTest(){
        View logChangeButton = solo.getView(R.id.log_change_button);
        solo.clickOnView(logChangeButton);

        solo.enterText((EditText) solo.getView(R.id.reg_email),"logintenttests@email.com");
        solo.enterText((EditText) solo.getView(R.id.reg_username),"LogIntentUser");
        solo.enterText((EditText) solo.getView(R.id.reg_pass),"LogIntentPass");
        solo.enterText((EditText) solo.getView(R.id.reg_pass_confirm),"LogIntentPass");

        View registerButton = solo.getView(R.id.reg_button);
        solo.clickOnView(registerButton);

        // assert that the MainActivity is now open
        solo.assertCurrentActivity("Wrong Activity",MainActivity.class);

        // assert that the correct user is logged in
        assertEquals("logintenttests@email.com",
                FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // delete the user just created so that the test can be run multiple times
        userCollectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .delete()
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Successfully deleted user!"))
                .addOnFailureListener(e -> Log.d(TAG, "Something went wrong!"));

        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"User account deleted");
                        }
                    }
                });
    }

    @Test
    public void registerErrorTest(){
        View logChangeButton = solo.getView(R.id.log_change_button);
        solo.clickOnView(logChangeButton);

        // ERROR 1: Passwords do not match
        solo.enterText((EditText) solo.getView(R.id.reg_email),"newuser@email.com");
        solo.enterText((EditText) solo.getView(R.id.reg_username),"newUser");
        solo.enterText((EditText) solo.getView(R.id.reg_pass),"Password");
        solo.enterText((EditText) solo.getView(R.id.reg_pass_confirm),"Incorrect");

        View registerButton = solo.getView(R.id.reg_button);
        solo.clickOnView(registerButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("Passwords do not match.",
                1,10000));
        solo.clickOnButton("Ok");

        // ERROR 2: Invalid email address

        solo.clearEditText((EditText) solo.getView(R.id.reg_email));
        solo.clearEditText((EditText) solo.getView(R.id.reg_username));
        solo.clearEditText((EditText) solo.getView(R.id.reg_pass));
        solo.clearEditText((EditText) solo.getView(R.id.reg_pass_confirm));

        solo.enterText((EditText) solo.getView(R.id.reg_email),"newuser");
        solo.enterText((EditText) solo.getView(R.id.reg_username),"newUser");
        solo.enterText((EditText) solo.getView(R.id.reg_pass),"Password");
        solo.enterText((EditText) solo.getView(R.id.reg_pass_confirm),"Password");

        solo.clickOnView(registerButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("Invalid email address.",
                1,10000));
        solo.clickOnButton("Ok");

        // ERROR 3: Email address is already associated with another account

        solo.clearEditText((EditText) solo.getView(R.id.reg_email));
        solo.clearEditText((EditText) solo.getView(R.id.reg_username));
        solo.clearEditText((EditText) solo.getView(R.id.reg_pass));
        solo.clearEditText((EditText) solo.getView(R.id.reg_pass_confirm));

        solo.enterText((EditText) solo.getView(R.id.reg_email),defaultEmail);
        solo.enterText((EditText) solo.getView(R.id.reg_username),"newUser");
        solo.enterText((EditText) solo.getView(R.id.reg_pass),"Password");
        solo.enterText((EditText) solo.getView(R.id.reg_pass_confirm),"Password");

        solo.clickOnView(registerButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("The email address is already in use by another account.",
                1,10000));
        solo.clickOnButton("Ok");

        // ERROR 4: Username is already associated with another account

        solo.clearEditText((EditText) solo.getView(R.id.reg_email));
        solo.clearEditText((EditText) solo.getView(R.id.reg_username));
        solo.clearEditText((EditText) solo.getView(R.id.reg_pass));
        solo.clearEditText((EditText) solo.getView(R.id.reg_pass_confirm));

        solo.enterText((EditText) solo.getView(R.id.reg_email),"newemail@email.com");
        solo.enterText((EditText) solo.getView(R.id.reg_username),defaultName);
        solo.enterText((EditText) solo.getView(R.id.reg_pass),"Password");
        solo.enterText((EditText) solo.getView(R.id.reg_pass_confirm),"Password");

        solo.clickOnView(registerButton);
        solo.waitForDialogToOpen();

        assertTrue(solo.waitForText("User with this username already exists.",
                1,10000));
        solo.clickOnButton("Ok");

    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        // ensure that any users logged in through testing are logged out
        FirebaseAuth.getInstance().signOut();
        solo.finishOpenedActivities();
    }
}
