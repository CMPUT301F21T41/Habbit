package com.example.habbit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.activities.LogInActivity;
import com.example.habbit.activities.MainActivity;
import com.example.habbit.activities.ProfileActivity;
import com.example.habbit.fragments.LogInFragment;
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
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // ensure that there is no user signed in before testing began
        FirebaseAuth.getInstance().signOut();
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
