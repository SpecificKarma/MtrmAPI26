package com.gmail.specifickarma.metronome.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.gmail.specifickarma.metronome.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class TestFlashLightFunction {
    @Test
    public void verifyLightFunOnMinBPM() {
        ActivityScenario.launch(Home.class);
        onView(withId(R.id.power)).perform(click());
        onView(withId(R.id.s_trans)).perform(click());
        onView(withId(R.id.cursor_on)).perform(Util.swipeSetToStartToMin());
        onView(withId(R.id.textView)).check(matches(withText(" 40")));
    }

    @Test
    public void verifyLightFunOnMidBPM() {
        ActivityScenario.launch(Home.class);
        onView(withId(R.id.power)).perform(click());
        onView(withId(R.id.s_trans)).perform(click());
        onView(withId(R.id.cursor_on)).perform(Util.swipeSetToStartToMid());
        onView(withId(R.id.textView)).check(matches(withText(" 121")));
    }

    @Test
    public void verifyLightFunOnMaxBPM() {
        ActivityScenario.launch(Home.class);
        onView(withId(R.id.power)).perform(click());
        onView(withId(R.id.s_trans)).perform(click());
        onView(withId(R.id.cursor_on)).perform(Util.swipeSetToStartToMid());
        onView(withId(R.id.cursor_on)).perform(Util.swipeSetToMidToMax());
        onView(withId(R.id.textView)).check(matches(withText(" 218")));
    }
}