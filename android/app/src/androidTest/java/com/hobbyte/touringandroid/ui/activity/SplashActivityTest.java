package com.hobbyte.touringandroid.ui.activity;

import android.database.Cursor;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.After;

import static org.junit.Assert.assertEquals;

/**
 * {@link SplashActivity} starts {@link com.hobbyte.touringandroid.internet.UpdateChecker} (which is
 * tested elsewhere), and then deletes any tours that are out of date.
 * <p>
 * Thanks go to <a href="https://jabknowsnothing.wordpress.com/2015/11/05/activitytestrule-espressos-test-lifecycle/">
 * this site</a> for explaining the lifecycle of {@link ActivityTestRule}.
 */
@RunWith(AndroidJUnit4.class)
public class SplashActivityTest {

    // premade key/tour on the server
    private static final String KEY_ID = "5D8LIPxDBG";
    private static final String TOUR_ID = "N3l3tuYFka";
    private static final String KEY_NAME = "KCL-TEST-EXPIRED";
    private static final String TOUR_NAME = "TEST_EXPIRED_TOUR";
    private static final String KEY_EXPIRY = "2016-01-01T00:00:00.000Z";

    @Rule
    public ActivityTestRule<SplashActivity> activityRule = new ActivityTestRule<SplashActivity>(
            SplashActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();

            // need to override this method to make sure that this row is in the DB before
            // the activity is created
            TourDBManager dbHelper = TourDBManager.getInstance(App.context);
            dbHelper.putRow(KEY_ID, TOUR_ID, KEY_NAME, TOUR_NAME, KEY_EXPIRY, false, 1);
        }
    };

    /**
     * Wait for SplashActivity to finish updating/deleting.
     */
    @Before
    public void setUp() {
        while (!activityRule.getActivity().isFinished()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Make sure that the expired tour in the db is deleted. (The activity will also try to delete
     * non-existing tour files, but that's not an issue).
     */
    @Test
    public void doesExpiredTourGetDeleted() {
        TourDBManager dbHelper = TourDBManager.getInstance(App.context);

        Cursor c = dbHelper.getRow(KEY_ID);
        int count = c.getCount();
        c.close();

        assertEquals(0, count);
    }

    /**
     * Delete the row from the DB in case it wasn't removed in the activity as it was supposed
     * to be.
     */
    @After
    public void tearDown() {
        TourDBManager dbHelper = TourDBManager.getInstance(App.context);
        dbHelper.deleteTour(KEY_ID);
        dbHelper.close();
    }



}
