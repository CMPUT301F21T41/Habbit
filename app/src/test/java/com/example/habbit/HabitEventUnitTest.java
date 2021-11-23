package com.example.habbit;

import static org.junit.Assert.assertEquals;

import com.example.habbit.models.HabitEvent;

import org.junit.Test;

public class HabitEventUnitTest {
    String sampleComment = "Sample Comment";

    private HabitEvent mockHabitEvent() {
        return new HabitEvent(sampleComment);
    }

    @Test
    public void testGetters() {
        HabitEvent habitEvent = mockHabitEvent();

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
        habitEvent.setId(editedId);
        assertEquals(editedId, habitEvent.getId());
    }
}
