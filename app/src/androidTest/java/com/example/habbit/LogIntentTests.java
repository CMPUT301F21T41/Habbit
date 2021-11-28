package com.example.habbit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    // the user credentials of our pre-made dummy account
    private String defaultUid = "AlF39kSveNM3BYaUmSQfWqvtsxt1";
    private String defaultEmail = "default@default.com";
    private String defaultPass = "defaultPass";

    @Rule
    public ActivityTestRule<LogInActivity> rule = new ActivityTestRule<>(LogInActivity.class, true, true);

    /**
     * Runs before all tests and creates instance of solo
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        FirebaseAuth.getInstance().signOut();
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // ensure that there is no user signed in before testing began
//        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Get activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Testing a successful log in
     */
    @Test
    public void logInSuccessTest(){
        // asserts that the current activity is LogInActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity",LogInActivity.class);

        // asserts that there is no user signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertEquals(null,user);

        // asserts that the LogInFragment is opened (with the title text 'LOGIN')
        assertTrue(solo.searchText("LOGIN"));

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
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Log.i("LogIntentTests",FirebaseAuth.getInstance().getCurrentUser().getEmail());
            FirebaseAuth.getInstance().signOut();
        }
        // asserts that the current activity is LogInActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity",LogInActivity.class);

        // asserts that there is no user signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertEquals(null,user);

        // asserts that the LogInFragment is opened (with the title text 'LOGIN')
        assertTrue(solo.searchText("LOGIN"));

        // ERROR 1: correct user email, incorrect password
        solo.enterText((EditText) solo.getView(R.id.log_email),defaultEmail);
        solo.enterText((EditText) solo.getView(R.id.log_pass),"incorrectPass");

        View logInButton = solo.getView(R.id.log_button);
        solo.clickOnView(logInButton);

        assertTrue(solo.waitForText("Invalid Password",1,2000));
        solo.clickOnButton("Ok");

        // ERROR 2: User account is not found
        // i.e., invalid email input (xxx@xxx.xx not followed) or
        // valid email not associated with any user
        solo.enterText((EditText) solo.getView(R.id.log_email),"defaultEmail");
        solo.enterText((EditText) solo.getView(R.id.log_pass),"incorrectPass");
        solo.clickOnView(logInButton);

        assertTrue(solo.waitForText("User not found",1,2000));
        solo.clickOnButton("Ok");
    }

    @Test
    public void switchFragTest(){
        // asserts that the current activity is LogInActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity",LogInActivity.class);

        // asserts that there is no user signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertEquals(null,user);

        // asserts that the LogInFragment is opened (with the title text 'LOGIN')
        assertTrue(solo.searchText("LOGIN"));

        View logChangeButton = solo.getView(R.id.log_change_button);
        solo.clickOnView(logChangeButton);

        // assert that the RegisterFragment is open
        assertTrue(solo.waitForView(R.id.reg_email,1,2000));

        View regChangeButton = solo.getView(R.id.reg_change_button);
        solo.clickOnView(regChangeButton);

        // assert that the LogInFragment is open
        assertTrue(solo.waitForView(R.id.log_email,1,2000));
    }

    @Test
    public void registerSuccessTest(){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Log.i("LogIntentTests",FirebaseAuth.getInstance().getCurrentUser().getEmail());
            FirebaseAuth.getInstance().signOut();
        }
        // asserts that the current activity is LogInActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity",LogInActivity.class);

        // asserts that there is no user signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertEquals(null,user);

        // asserts that the LogInFragment is opened (with the title text 'LOGIN')
        assertTrue(solo.searchText("LOGIN"));

        View logChangeButton = solo.getView(R.id.log_change_button);
        solo.clickOnView(logChangeButton);

        // assert that the RegisterFragment is open
//        assertTrue(solo.waitForView(R.id.reg_email,1,5000));

        solo.enterText((EditText) solo.getView(R.id.reg_email),"logintenttests@email.com");
        solo.enterText((EditText) solo.getView(R.id.reg_username),"LogIntentUser");
        solo.enterText((EditText) solo.getView(R.id.reg_pass),"LogIntentPass");
        solo.enterText((EditText) solo.getView(R.id.reg_pass_confirm),"LogIntentPass");

        View registerButton = solo.getView(R.id.reg_button);
        solo.clickOnView(registerButton);

        // assert that the MainActivity is now open
        solo.assertCurrentActivity("Wrong Activity",MainActivity.class);

        // assert that the correct user is logged in
        assertEquals("logintenttests@email.com",FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // delete the user just created so that the test can be run multiple times
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("LogIntentTests","User account deleted.");
                        }
                    }
                });
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
