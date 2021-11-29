package com.example.habbit;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habbit.activities.HabitEventsActivity;
import com.example.habbit.activities.MainActivity;
import com.example.habbit.activities.ProfileActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for MainActivity. All UI tests written here, using Robotium
 */
public class IntentTests {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates instance of solo
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
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
     * Testing add, edit, delete habit
     * Test all three functionalities together because in order to edit a habit, you must add a habit first
     * In order to delete a habit, you must add a habit first
     */
    @Test
    public void habitListTest() {
        // asserts that the current activity is MainActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click on add habit button
        View addHabitButton = solo.getView(R.id.add_habit_button);
        solo.clickOnView(addHabitButton);

        // get calendar view
        View editDateView = solo.getView(R.id.edit_habit_date);

        // get view for edit text for each field and enter in values
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Sample Title");
        solo.clickOnView(editDateView);
        solo.setDatePicker(0, 2021, 2, 16);
        solo.clickOnText("OK");
        solo.enterText((EditText) solo.getView(R.id.edit_habit_reason), "Sample Habit Reason");
        solo.clickOnButton("Confirm");

        // true if there is "Sample title" on the screen
        assertTrue(solo.waitForText("Sample Title", 1, 2000));

        // verify habit details
        solo.clickInList(1);
        assertTrue(solo.waitForText("Sample Title", 1, 2000));
        assertTrue(solo.searchText("2021-03-16"));
        assertTrue(solo.searchText("Sample Habit Reason"));
        solo.clickOnText("Close");

        // edit the habit
        solo.clickInList(1);
        solo.clickOnText("Edit");
        solo.clearEditText((EditText) solo.getView(R.id.edit_habit_title));
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Edited Title");
        solo.clearEditText((EditText) solo.getView(R.id.edit_habit_date));
        solo.clickOnView(editDateView);
        solo.setDatePicker(0, 2021, 3, 16);
        solo.clickOnText("OK");
        solo.clearEditText((EditText) solo.getView(R.id.edit_habit_reason));
        solo.enterText((EditText) solo.getView(R.id.edit_habit_reason), "Edited Habit Reason");
        solo.clickOnButton("Confirm");

        // verify edited habit
        solo.clickInList(1);
        assertTrue(solo.waitForText("Edited Title", 1, 2000));
        assertTrue(solo.searchText("2021-04-16"));
        assertTrue(solo.searchText("Edited Habit Reason"));
        solo.clickOnText("Close");

        // delete item from list
        solo.clickInList(1);
        solo.clickOnText("Delete");
        assertFalse(solo.searchText("Sample Title"));
    }

    /**
     * Test clicking view habit events button
     */
    @Test
    public void checkViewHabitEvents() {
        // asserts that the current activity is MainActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click on add habit button
        View addHabitButton = solo.getView(R.id.add_habit_button);
        solo.clickOnView(addHabitButton);

        // get calendar view
        View editDateView = solo.getView(R.id.edit_habit_date);

        // get view for edit text for each field and enter in values
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Sample Title");
        solo.clickOnView(editDateView);
        solo.setDatePicker(0, 2021, 2, 16);
        solo.clickOnText("OK");
        solo.enterText((EditText) solo.getView(R.id.edit_habit_reason), "Sample Habit Reason");
        solo.clickOnButton("Confirm");

        // view habit events button
        solo.clickInList(1);
        solo.clickOnButton("See Habit Events");

        // check that we have entered a new activity
        solo.assertCurrentActivity("Wrong Activity", HabitEventsActivity.class);

        // go back
        solo.goBack();
        solo.clickInList(1);
        solo.clickOnText("Delete");
    }

    /**
     * Test what happens when you click the checkbox
     */
    @Test
    public void habitEventsListTest() {
        // asserts that the current activity is MainActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click on add habit button
        View addHabitButton = solo.getView(R.id.add_habit_button);
        solo.clickOnView(addHabitButton);

        // get calendar view
        View editDateView = solo.getView(R.id.edit_habit_date);

        // get view for edit text for each field and enter in values
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Sample Title");
        solo.clickOnView(editDateView);
        solo.setDatePicker(0, 2021, 2, 16);
        solo.clickOnText("OK");
        solo.enterText((EditText) solo.getView(R.id.edit_habit_reason), "Sample Habit Reason");
        solo.clickOnButton("Confirm");

        // click the checkbox
        solo.clickOnCheckBox(0);

        // hit confirm to see that we have logged a habit event
        solo.enterText((EditText) solo.getView(R.id.edit_habit_event_comment), "Sample comment");
        solo.clickOnText("Confirm");

        // view habit events button
        solo.clickInList(1);
        solo.clickOnButton("See Habit Events");

        // check that we have entered a new activity
        solo.assertCurrentActivity("Wrong Activity", HabitEventsActivity.class);

        // check that we have logged a habit event
        assertTrue(solo.waitForText("Sample comment", 1, 2000));

        // check habit event details
        solo.clickInList(0);
        assertTrue(solo.waitForText("Sample comment", 1, 2000));

        // edit habit event details
        solo.clickOnText("Edit");
        solo.clearEditText((EditText) solo.getView(R.id.edit_habit_event_comment));
        solo.enterText((EditText) solo.getView(R.id.edit_habit_event_comment), "Edited comment");
        solo.clickOnButton("Confirm");

        // verify it was edited
        solo.clickInList(0);
        assertTrue(solo.waitForText("Edited comment", 1, 2000));

        // delete the habit event
        solo.clickOnText("Delete");
        assertFalse(solo.searchText("Edited comment"));

        // go back
        solo.goBack();
        solo.clickInList(0);
        solo.clickOnText("Delete");
    }

    /**
     * Check profile nav button
     */
    @Test
    public void profileNavTest() {
        // asserts that the current activity is MainActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click on profile nav button
        View profileNavButton = solo.getView(R.id.profile);
        solo.clickOnView(profileNavButton);

        // check that we have entered a new activity
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
