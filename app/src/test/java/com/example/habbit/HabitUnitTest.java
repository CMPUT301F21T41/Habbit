package com.example.habbit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.habbit.models.Habit;
import com.example.habbit.models.HabitEvent;

import org.junit.Test;

public class HabitUnitTest {

    String sampleTitle = "Habit title";
    String sampleReason = "Habit reason";
    String sampleDate = "2020-04-12";

    private Habit mockHabit() {
        return new Habit(sampleTitle, sampleReason, sampleDate);
    }

    @Test
    public void testGetters() {
        Habit habit = mockHabit();
        assertEquals(sampleTitle, habit.getTitle());
        assertEquals(sampleReason, habit.getReason());
        assertEquals(sampleDate, habit.getDate());
    }

    @Test
    public void testSetters() {
        String editedTitle = "Edited title";
        String editedReason = "Edited reason";
        String editedDate = "2020-04-17";
        String editedId = "ABC";
        boolean editedChecked = true;

        Habit habit = mockHabit();
        habit.setTitle(editedTitle);
        assertEquals(editedTitle, habit.getTitle());
        habit.setReason(editedReason);
        assertEquals(editedReason, habit.getReason());
        habit.setDate(editedDate);
        assertEquals(editedDate, habit.getDate());
        habit.setId(editedId);
        assertEquals(editedId, habit.getId());
        habit.setChecked(editedChecked);
        assertEquals(editedChecked, habit.isChecked());
    }

    @Test
    public void testAddingHabitEvents() {
        Habit habit = mockHabit();
        HabitEvent habitEvent = new HabitEvent("Habit event comment");

        assertTrue(habit.getHabitEvents().isEmpty());
        habit.addHabitEvent(habitEvent);
        assertTrue(habit.getHabitEvents().contains(habitEvent));
        habit.clearHabitEvents();
        assertTrue(habit.getHabitEvents().isEmpty());
    }
}
