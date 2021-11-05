package com.example.habbit;

import static org.junit.Assert.assertEquals;

import com.example.habbit.models.HabitEvent;

import org.junit.Test;

public class HabitEventUnitTest {
    String sampleComment = "Sample Comment";
    boolean sampleCOT = false;

    private HabitEvent mockHabitEvent() {
        return new HabitEvent(sampleCOT, sampleComment);
    }

    @Test
    public void testGetters() {
        HabitEvent habitEvent = mockHabitEvent();

        assertEquals(sampleCOT, habitEvent.isCompletedOnTime());
        assertEquals(sampleComment, habitEvent.getComment());
    }

    @Test
    public void testSetters() {
        String editedComment = "Edited Comment";
        boolean editedCOT = true;
        String editedId = "ABC";

        HabitEvent habitEvent = mockHabitEvent();

        habitEvent.setComment(editedComment);
        assertEquals(editedComment, habitEvent.getComment());
        habitEvent.setCompletedOnTime(editedCOT);
        assertEquals(editedCOT, habitEvent.isCompletedOnTime());
        habitEvent.setId(editedId);
        assertEquals(editedId, habitEvent.getId());
    }
}
