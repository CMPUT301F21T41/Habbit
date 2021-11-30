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
import com.example.habbit.activities.MapActivity;
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

        // get day picker toggle views
        View dayPickerSun = solo.getView(R.id.tSun);
        View dayPickerMon = solo.getView(R.id.tMon);
        View dayPickerTue = solo.getView(R.id.tTue);
        View dayPickerWed = solo.getView(R.id.tWed);
        View dayPickerThu = solo.getView(R.id.tThu);
        View dayPickerFri = solo.getView(R.id.tFri);
        View dayPickerSat = solo.getView(R.id.tSat);


        // get privacy toggle
        View privacyToggle = solo.getView(R.id.edit_publicity_switch);

        // get calendar view
        View editDateView = solo.getView(R.id.edit_habit_date);

        // get view for edit text for each field and enter in values
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Sample Title");
        solo.clickOnView(privacyToggle);

        solo.clickOnView(dayPickerSun);
        solo.clickOnView(dayPickerMon);
        solo.clickOnView(dayPickerTue);
        solo.clickOnView(dayPickerWed);
        solo.clickOnView(dayPickerThu);
        solo.clickOnView(dayPickerFri);
        solo.clickOnView(dayPickerSat);

        solo.clickOnView(editDateView);

        solo.setDatePicker(0, 2021, 2, 16);
        solo.clickOnText("OK");
        solo.enterText((EditText) solo.getView(R.id.edit_habit_reason), "Sample Habit Reason");
        solo.clickOnButton("Confirm");

        // true if there is "Sample title" on the screen
        assertTrue(solo.waitForText("Sample Title", 1, 2000));

        // verify habit details
        solo.clickOnText("Sample Title");
        assertTrue(solo.waitForText("Sample Title", 1, 2000));
        assertTrue(solo.searchText("Public"));
        assertTrue(solo.searchText("2021-03-16"));
        assertTrue(solo.searchText("Sample Habit Reason"));
        solo.clickOnText("Close");

        // edit the habit
        solo.clickOnText("Sample Title");
        solo.clickOnText("Edit");
        solo.clearEditText((EditText) solo.getView(R.id.edit_habit_title));
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Edited Title");

        solo.clearEditText((EditText) solo.getView(R.id.edit_habit_reason));
        solo.enterText((EditText) solo.getView(R.id.edit_habit_reason), "Edited Habit Reason");
        solo.clickOnButton("Confirm");

        // verify edited habit
        solo.clickOnText("Edited Title");
        assertTrue(solo.waitForText("Edited Title", 1, 2000));

        assertTrue(solo.searchText("Edited Habit Reason"));
        solo.clickOnText("Close");

        // delete item from list
        solo.clickOnText("Edited Title");
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

        // get privacy toggle
        View privacyToggle = solo.getView(R.id.edit_publicity_switch);

        // get day picker toggle views
        View dayPickerSun = solo.getView(R.id.tSun);
        View dayPickerMon = solo.getView(R.id.tMon);
        View dayPickerTue = solo.getView(R.id.tTue);
        View dayPickerWed = solo.getView(R.id.tWed);
        View dayPickerThu = solo.getView(R.id.tThu);
        View dayPickerFri = solo.getView(R.id.tFri);
        View dayPickerSat = solo.getView(R.id.tSat);

        // get view for edit text for each field and enter in values
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Sample Title");

        solo.clickOnView(privacyToggle);

        solo.clickOnView(dayPickerSun);
        solo.clickOnView(dayPickerMon);
        solo.clickOnView(dayPickerTue);
        solo.clickOnView(dayPickerWed);
        solo.clickOnView(dayPickerThu);
        solo.clickOnView(dayPickerFri);
        solo.clickOnView(dayPickerSat);

        solo.clickOnView(editDateView);
        solo.setDatePicker(0, 2021, 2, 16);
        solo.clickOnText("OK");
        solo.enterText((EditText) solo.getView(R.id.edit_habit_reason), "Sample Habit Reason");
        solo.clickOnButton("Confirm");

        // view habit events button
        solo.clickOnText("Sample Title");
        solo.clickOnText("View Habit Events");

        // check that we have entered a new activity
        solo.assertCurrentActivity("Wrong Activity", HabitEventsActivity.class);

        // go back
        solo.goBack();
        solo.clickOnText("Sample Title");
        solo.clickOnText("Delete");
    }

    /**
     * Test what happens when you click the checkbox
     */
    @Test
    public void habitEventsListTest() {
        // asserts that the current activity is MainActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnText("All Habits");
        solo.clickOnText("Today's Habits");

        // click on add habit button
        View addHabitButton = solo.getView(R.id.add_habit_button);
        solo.clickOnView(addHabitButton);

        // get calendar view
        View editDateView = solo.getView(R.id.edit_habit_date);


        // get privacy toggle
        View privacyToggle = solo.getView(R.id.edit_publicity_switch);

        // get day picker toggle views
        View dayPickerSun = solo.getView(R.id.tSun);
        View dayPickerMon = solo.getView(R.id.tMon);
        View dayPickerTue = solo.getView(R.id.tTue);
        View dayPickerWed = solo.getView(R.id.tWed);
        View dayPickerThu = solo.getView(R.id.tThu);
        View dayPickerFri = solo.getView(R.id.tFri);
        View dayPickerSat = solo.getView(R.id.tSat);

        // get view for edit text for each field and enter in values
        solo.enterText((EditText) solo.getView(R.id.edit_habit_title), "Sample Title");

        solo.clickOnView(privacyToggle);

        solo.clickOnView(dayPickerSun);
        solo.clickOnView(dayPickerMon);
        solo.clickOnView(dayPickerTue);
        solo.clickOnView(dayPickerWed);
        solo.clickOnView(dayPickerThu);
        solo.clickOnView(dayPickerFri);
        solo.clickOnView(dayPickerSat);

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
        solo.clickOnText("Sample Title");
        solo.clickOnText("View Habit Events");

        // check that we have entered a new activity
        solo.assertCurrentActivity("Wrong Activity", HabitEventsActivity.class);

        // check that we have logged a habit event
        assertTrue(solo.waitForText("Sample comment", 1, 2000));

        // check habit event details
        solo.clickOnText("Sample comment");
        assertTrue(solo.waitForText("Sample comment", 1, 2000));

        // edit habit event details
        solo.clickOnText("Edit");
        solo.clearEditText((EditText) solo.getView(R.id.edit_habit_event_comment));
        solo.enterText((EditText) solo.getView(R.id.edit_habit_event_comment), "Edited comment");
        solo.clickOnButton("Confirm");

        // verify it was edited
        solo.clickOnText("Edited comment");
        assertTrue(solo.waitForText("Edited comment", 1, 2000));

        // delete the habit event
        solo.clickOnText("Delete");
        assertFalse(solo.searchText("Edited comment"));

        // go back
        solo.goBack();
        solo.clickOnText("Sample Title");
        solo.clickOnText("Delete");
    }

    /**
     * Check Map Activity functionality
     */
    @Test
    public void mapActivityTest() {
        // asserts that the current activity is MainActivity, otherwise notify that it is the wrong activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        //change view to day view
        solo.clickOnText("All Habits");
        solo.waitForText("Today's Habits");
        solo.clickOnText("Today's Habits");
        //go to habit event entry
        solo.clickOnCheckBox(0);
        // click on add location button
        View addLocLink = solo.getView(R.id.add_location_link);
        solo.clickOnView(addLocLink);
        // check that we have entered a new activity
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
        //wait for location found message
        assertTrue(solo.waitForText("Click me to confirm location, or double-tap to select another!"));
        //confirm pin is on screen
        Boolean isVisible = (Boolean) solo.getCurrentActivity().getResources().getDrawable(R.drawable.ic__192595).isVisible();
        assertTrue(isVisible);
        //confirm first message is displayed
        assertTrue(solo.searchText("Click me to confirm location, or double-tap to select another!"));
        //click center of screen to confrim current location
        solo.clickOnImage(R.drawable.ic__192595);
        //make sure confirmation message is displayed
        assertTrue(solo.searchText("Click Me Again to Confirm Location!"));
        //click again to finalize
        solo.clickOnImage(R.drawable.ic__192595);
        //confirm map exit and city matches location picked on fragment
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
        assertTrue(solo.searchText("Edmonton, Alberta"));
        //click confirm
        solo.clickOnText("CONFIRM");
        //navigate to habit event view
        solo.clickInList(0);
        solo.clickOnText("View Habit Events");
        //make sure we are in habit events activity
        solo.assertCurrentActivity("Wrong Activity!", HabitEventsActivity.class);
        //confirm that the city shows up in the listview
        assertTrue(solo.searchText("Edmonton, Alberta"));
        //navigate to habitevent view
        solo.clickInList(0);
        //make sure curretn city shows up in habitevent view
        solo.searchText("Edmonton, Alberta");





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
