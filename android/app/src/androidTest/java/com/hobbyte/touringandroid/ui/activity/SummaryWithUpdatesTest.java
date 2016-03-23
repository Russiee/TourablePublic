package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;
import com.hobbyte.touringandroid.FakeJSON;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

/**
 * Small tests to ensure that the activity displays the correct information when the tour in
 * question has an update available.
 */
@RunWith(AndroidJUnit4.class)
public class SummaryWithUpdatesTest {

    private static final String KEY_ID = "qcyq1p1Yje";
    private static final String TOUR_ID = "j87HWsX5fh";
    private static final String KEY_NAME = "KCL-TEST-UPDATED";
    private static final String EXPIRY = "2018-01-01T00:00:00.000Z";

    private TourDBManager db;
    private Intent intent;

    @Rule
    public ActivityTestRule<SummaryActivity> activityRule = new ActivityTestRule<>(
            SummaryActivity.class, true, false
    );

    @Before
    public void setUp() throws Exception {
        FileManager.makeTourDirectories(App.context, KEY_ID);
        JSONObject json = new JSONObject(FakeJSON.TOUR_JSON);
        FileManager.saveJSON(json, KEY_ID, FileManager.TOUR_JSON);

        db = TourDBManager.getInstance(App.context);
        db.putRow(KEY_ID, TOUR_ID, KEY_NAME, "test-tour", EXPIRY, true, 3);
        db.flagTourUpdate(KEY_ID, true);

        intent = new Intent();
        intent.putExtra(SummaryActivity.KEY_ID, KEY_ID);
        intent.putExtra(SummaryActivity.TOUR_ID, TOUR_ID);
        intent.putExtra(SummaryActivity.KEY_NAME, KEY_NAME);

        activityRule.launchActivity(intent);
    }

    /**
     * Make sure that the 'up to date' text has changed to reflect the fact that there is an
     * update available.
     */
    @Test
    public void hasUpdateText() {
        onView(withId(R.id.txtVersion)).check(
                matches(withText(R.string.summary_activity_new_version_is_available))
        );
    }

    /**
     * Make sure that the button to update is clickable (which it isn't normally).
     */
    @Test
    public void hasUpdateIcon() {
        onView(withId(R.id.updateTour)).check(matches(isClickable()));
    }

    /**
     * Make sure that the download dialog pops up when you click on the 'update' button.
     */
    @Test
    public void hasDownloadDialog() {
        onView(withId(R.id.updateTour)).perform(click());
        onView(withText(R.string.download_dialog_download_tour)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        FileManager.removeTour(App.context, KEY_ID);
        db.close();
    }
}
