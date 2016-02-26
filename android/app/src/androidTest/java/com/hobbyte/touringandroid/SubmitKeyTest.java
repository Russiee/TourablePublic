package com.hobbyte.touringandroid;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hobbyte.touringandroid.ui.activity.StartActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test the functionality of the text field where the user enters a tour key.
 */
@RunWith(AndroidJUnit4.class)
public class SubmitKeyTest {

    private String prefs_key;
    private String prefs_current_tour;

    @Rule
    public ActivityTestRule<StartActivity> activityRule = new ActivityTestRule<>(
            StartActivity.class);

    @Before
    public void assignStrings() {
        prefs_key = activityRule.getActivity().getString(R.string.preference_file_key);
        prefs_current_tour= activityRule.getActivity().getString(R.string.prefs_current_tour);
    }

    @Test
    public void submitInvalidKey() {
        // click FAB to bring up key entry form
        onView(withId(R.id.fab)).perform(click());

        // enter a bad key into the entry form and click submit
        onView(withId(R.id.textEnterTour)).perform(typeText("fakeKey"), closeSoftKeyboard());
        onView(withId(R.id.buttonSubmitKey)).perform(click());

        // detecting a Toast is difficult, so for now check that no tour exists in SharedPreferences
        boolean noTour = activityRule.getActivity().getSharedPreferences(
                prefs_key, Context.MODE_PRIVATE
        ).contains(prefs_current_tour);

        assertFalse(noTour);
    }
}