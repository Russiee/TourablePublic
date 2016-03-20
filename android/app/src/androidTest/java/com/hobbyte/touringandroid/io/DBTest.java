package com.hobbyte.touringandroid.io;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Instrumented unit tests for methods in {@link TourDBManager}. Note that these tests are purely
 * concerned with whether the methods return the correct results for a given db state. Issues with
 * concurrency are not addressed. However, the app makes very light use of the db, and avoiding
 * concurrency issues has been trivial.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DBTest {
    private TourDBManager db;

    private static String KEYID_1 = "qwerty";
    private static String KEYID_2 = "azerty";

    private static String TOURID_1 = "1kdlNd7";
    private static String TOURID_2 = "pLd3B8d";

    private static String NAME_1 = "iLuvTestsTour";
    private static String NAME_2 = "iHateTestsTour";

    private static String UPDATED_1 = "2016-02-25T12:32:06.456Z";
    private static String UPDATED_2 = "2015-12-25T12:32:06.032Z";
    private static long UPDATED_1_L = 1456403526456L;
    private static long UPDATED_2_L = 1451046726032L;

    private static String EXPIRES_IN_FUTURE = "2017-04-01T00:00:00.000Z";
    private static String EXPIRED_IN_PAST = "2016-03-01T00:00:00.000Z";
    private static long EXPIRES_IN_FUTURE_L = 1491004800000L;
    private static long EXPIRED_IN_PAST_L = 1456790400000L;

    @Before
    public void setUp() {
        // this will cause a fake database to be used
        RenamingDelegatingContext context = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_"
        );

        db = TourDBManager.getInstance(context);
    }

    @Test
    public void testEmpty() {
        assertTrue(db.dbIsEmpty());

        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);
        assertFalse(db.dbIsEmpty());
    }

    @Test
    public void getToursRankedByAccessedDate() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);
        db.putRow(KEYID_2, TOURID_2, NAME_2, EXPIRES_IN_FUTURE, false, 1);

        Cursor c = db.getTourDisplayInfo();
        c.moveToFirst();
        String accessed = c.getString(0); c.close();

        // since the tour that was made second should have the more recent lastAccessed time,
        // it should appear first in the results
        assertEquals(KEYID_2, accessed);

        db.updateAccessedTime(KEYID_1);
        c = db.getTourDisplayInfo();
        c.moveToFirst();
        accessed = c.getString(0);

        // now the first tour should be the most recent
        assertEquals(KEYID_1, accessed);
    }

    @Test
    public void areRowsInsertedProperly_nonDates() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, true, 1);

        Cursor c = db.getRow(KEYID_1);
        c.moveToFirst();

        String kid = c.getString(0);
        String tid = c.getString(1);
        String name = c.getString(2);
        int hasMedia = c.getInt(5);
        c.close();

        assertEquals(KEYID_1, kid);
        assertEquals(TOURID_1, tid);
        assertEquals(NAME_1, name);
        assertEquals(1, hasMedia);
    }

    @Test
    public void areRowsInsertedProperly_dates() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, true, 1);

        Cursor c = db.getRow(KEYID_1);
        c.moveToFirst();

        long expires = c.getLong(3);
        long accessed = Calendar.getInstance().getTimeInMillis();
        long accessed_db = c.getLong(6);
        c.close();

        assertEquals(EXPIRES_IN_FUTURE_L, expires);
        assertTrue(accessed - accessed_db < 200);
    }

    @Test
    public void testRowDeletion_single() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);
        db.putRow(KEYID_2, TOURID_2, NAME_2, EXPIRES_IN_FUTURE, false, 1);

        db.deleteTour(KEYID_2);

        assertFalse(db.dbIsEmpty());

        Cursor c = db.getRow(KEYID_2);
        int count = c.getCount();
        c.close();

        assertEquals(0, count);
    }

    @Test
    public void testRowDeletion_multi() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);
        db.putRow(KEYID_2, TOURID_2, NAME_2, EXPIRES_IN_FUTURE, false, 1);

        String[] toDelete = {KEYID_1, KEYID_2};

        db.deleteTours(toDelete);
        assertTrue(db.dbIsEmpty());
    }

    @Test
    public void correctUpdateInfo() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);
        db.putRow(KEYID_2, TOURID_2, NAME_2, EXPIRES_IN_FUTURE, false, 1);

        Object[][] info = db.getTourUpdateInfo();
        assertEquals(2, info.length);

        Object[] row = info[0];
        assertEquals(KEYID_1, row[0]);
        assertEquals(TOURID_1, row[1]);
        assertEquals(UPDATED_1_L, (long) row[2]);

        row = info[1];
        assertEquals(KEYID_2, row[0]);
        assertEquals(TOURID_2, row[1]);
        assertEquals(UPDATED_2_L, (long) row[2]);
    }

    @Test
    public void testGetExpiredTours() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);
        db.putRow(KEYID_2, TOURID_2, NAME_2, EXPIRED_IN_PAST, false, 1);

        String[] expiredTours = db.getExpiredTours();

        assertEquals(1, expiredTours.length);
        assertEquals(KEYID_2, expiredTours[0]);
    }

    @Test
    public void testTourExists() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);

        boolean tour1Exists = db.doesTourExist(KEYID_1);
        assertTrue(tour1Exists);
    }

    @Test
    public void testTourDoesNotExist() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);

        boolean tour2Exists = db.doesTourExist(KEYID_2);
        assertFalse(tour2Exists);
    }

    @Test
    public void testTourHasVideo() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, true, 1);

        boolean hasMedia = db.doesTourHaveVideo(KEYID_1);
        assertTrue(hasMedia);
    }

    @Test
    public void testTourDoesNotHaveVideo() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);

        boolean hasMedia = db.doesTourHaveVideo(KEYID_1);
        assertFalse(hasMedia);
    }

    @Test
    public void cannotPutDuplicateKeyIDs() {
        db.putRow(KEYID_1, TOURID_1, NAME_1, EXPIRES_IN_FUTURE, false, 1);
        db.putRow(KEYID_1, TOURID_2, NAME_2, EXPIRES_IN_FUTURE, true, 1);

        Cursor c = db.getRow(KEYID_1);
        int count = c.getCount();
        c.close();

        assertEquals(1, count);
    }

    @After
    public void tearDown() {
        db.clearTable();
        db.close();
        db = null;
    }
}
