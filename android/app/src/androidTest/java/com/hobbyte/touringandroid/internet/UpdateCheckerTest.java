package com.hobbyte.touringandroid.internet;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.hobbyte.touringandroid.io.TourDBManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * {@link UpdateChecker} checks if:<ul>
 *     <li>a tour has a new version (i.e. update) available</li>
 *     <li>the key used to download the tour has had a change in expiry date</li>
 * </ul>
 * This test uses a premade test key/tour on the server to test correct functionality.
 */
@RunWith(AndroidJUnit4.class)
public class UpdateCheckerTest {

    private static final String KEY_CODE = "KCL-TEST-UPDATED";
    private static final String KEY_ID = "qcyq1p1Yje";
    private static final String TOUR_ID = "j87HWsX5fh";
    private static final String TOUR_NAME = "TEST_UPDATED_TOUR";
    private static final int OUT_OF_DATE = 2;
    private static final int UP_TO_DATE = 3;
    private static final String KEY_EXPIRY = "2018-01-01T00:00:00.000Z";
    private static String KEY_EXPIRY_ALT = "2017-04-01T00:00:00.000Z";

    private RenamingDelegatingContext context;
    private TourDBManager db;

    @Before
    public void setUp() {
        context = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        db = TourDBManager.getInstance(context);
    }

    @Test
    public void shouldBeFlaggedForUpdate() {
        db.putRow(KEY_ID, TOUR_ID, KEY_CODE, TOUR_NAME, KEY_EXPIRY, false, OUT_OF_DATE);

        UpdateChecker checker = new UpdateChecker(context);
        checker.start();

        try {
            checker.join();
        } catch (InterruptedException e) {
            assertTrue(false); // trigger failure
        }

        boolean hasUpdate = db.doesTourHaveUpdate(KEY_ID);
        assertTrue(hasUpdate);
    }

    @Test
    public void shouldNotBeFlaggedForUpdate() {
        db.putRow(KEY_ID, TOUR_ID, KEY_CODE, TOUR_NAME, KEY_EXPIRY, false, UP_TO_DATE);

        UpdateChecker checker = new UpdateChecker(context);
        checker.start();

        try {
            checker.join();
        } catch (InterruptedException e) {
            assertTrue(false); // trigger failure
        }

        boolean hasUpdate = db.doesTourHaveUpdate(KEY_ID);
        assertFalse(hasUpdate);
    }

    @Test
    public void expiryDateShouldNotChange() {
        db.putRow(KEY_ID, TOUR_ID, KEY_CODE, TOUR_NAME, KEY_EXPIRY, false, UP_TO_DATE);

        UpdateChecker checker = new UpdateChecker(context);
        checker.start();

        try {
            checker.join();
        } catch (InterruptedException e) {
            assertTrue(false); // trigger failure
        }

        long expiry = db.getExpiryDate(KEY_ID);
        long expiryCheck = 0L;

        try {
            expiryCheck = TourDBManager.convertStampToMillis(KEY_EXPIRY);
        } catch (ParseException e) {
            assertTrue(false); // trigger failure
        }

        assertEquals(expiryCheck, expiry);
    }

    @Test
    public void expiryDateShouldChange() {
        db.putRow(KEY_ID, TOUR_ID, KEY_CODE, TOUR_NAME, KEY_EXPIRY_ALT, false, UP_TO_DATE);

        UpdateChecker checker = new UpdateChecker(context);
        checker.start();

        try {
            checker.join();
        } catch (InterruptedException e) {
            assertTrue(false); // trigger failure
        }

        long expiry = db.getExpiryDate(KEY_ID);
        long expiryCheck = 0L;

        try {
            expiryCheck = TourDBManager.convertStampToMillis(KEY_EXPIRY);
        } catch (ParseException e) {
            assertTrue(false); // trigger failure
        }

        assertEquals(expiryCheck, expiry);
    }

    @After
    public void tearDown() {
        db.clearTable();
        db.close();
        db = null;
    }

}
