package com.gmail.specifickarma.metronome.activities;

import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.gmail.specifickarma.metronome.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class TestStarButton {

    @Rule
    public IntentsTestRule<Home> mActivityRule = new IntentsTestRule<>(
            Home.class);

    @Test
    public void testIntendRedirection(){
        ActivityScenario.launch(Home.class);
        onView(withId(R.id.star)).perform(click());

        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                toPackage("com.android.vending"),
                hasData(Uri.parse("market://details?id=com.gmail.specifickarma.metronome"))));
    }
}

