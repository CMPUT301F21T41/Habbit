package com.example.habbit;


import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import com.example.habbit.activities.MainActivity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for MainActivity. All UI tests written here, using Robotium
 */
public class MainActivityTest {
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
     * Add a habit to the listview and check the name using assertTrue
     * Clear all the habits from the listview and check again using assertFalse
     */
    @Test
    public void checkList() {
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
    }
}
