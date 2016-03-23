package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.FakeJSON;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Nikita on 23/03/2016.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SummaryActivityTest {

    private static final String KEY_ID = "qcyq1p1Yje";
    private static final String TOUR_ID = "j87HWsX5fh";
    private static final String KEY_NAME = "KCL-TEST-UPDATED";
    private static final String EXPIRY = "2018-01-01T00:00:00.000Z";

    private TourDBManager db;

    @Rule
    public ActivityTestRule<SummaryActivity> mActivityRule = new ActivityTestRule<>(
            SummaryActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        FileManager.makeTourDirectories(App.context, KEY_ID);
        JSONObject json = new JSONObject(FakeJSON.TOUR_JSON);
        FileManager.saveJSON(json, KEY_ID, FileManager.TOUR_JSON);

        db = TourDBManager.getInstance(App.context);
        db.putRow(KEY_ID, TOUR_ID, KEY_NAME, "test-tour", EXPIRY, true, 3);

        Intent intent = new Intent();
        intent.putExtra(SummaryActivity.KEY_ID, KEY_ID);
        intent.putExtra(SummaryActivity.TOUR_ID, TOUR_ID);
        intent.putExtra(SummaryActivity.KEY_NAME, KEY_NAME);

        mActivityRule.launchActivity(intent);
    }
    @Test
    public void HTestClickExistingTour() {
        onView(withId(R.id.buttonStartTour)).check(matches(isDisplayed()));
        onView(withId(R.id.txtTourDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.tourCard)).check(matches(isDisplayed()));
    }


}
