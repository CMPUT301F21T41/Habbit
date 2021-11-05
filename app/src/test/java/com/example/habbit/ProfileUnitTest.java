package com.example.habbit;

import static org.junit.Assert.assertEquals;

import com.example.habbit.models.Profile;

import org.junit.Test;

public class ProfileUnitTest {

    private Profile mockProfile() {
        return new Profile();
    }

    @Test
    public void checkGettersAndSetters() {
        Profile profile = mockProfile();
        String name = "Name";

        profile.setName(name);
        assertEquals(name, profile.getName());
    }
}
