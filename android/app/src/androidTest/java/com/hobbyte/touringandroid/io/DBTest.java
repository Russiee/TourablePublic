package com.hobbyte.touringandroid.io;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static org.junit.Assert.*;

/**
 * Instrumented unit tests for methods in {@link TourDBManager}. Note that these tests are purely
 * concerned with whether the methods return the correct results for a given db state. Issues with
 * concurrency are not addressed. However, the app makes very light use of the db, and avoiding
 * concurrency issues has been trivial.
 */
@RunWith(AndroidJUnit4.class)
public class DBTest {
    private TourDBManager db;

    private static String KEYID_1 = "qwerty";
    private static String KEYID_2 = "azerty";

    private static String KEY_NAME_1 = "key-1";
    private static String KEY_NAME_2 = "key-2";

    private static String TOURID_1 = "1kdlNd7";
    private static String TOURID_2 = "pLd3B8d";

    private static String NAME_1 = "iLuvTestsTour";
    private static String NAME_2 = "iHateTestsTour";

    private static String EXPIRES_IN_FUTURE = "2017-04-01T00:00:00.000Z";
    private static String EXPIRED_IN_PAST = "2016-03-01T00:00:00.000Z";
    private static long EXPIRES_IN_FUTURE_L = 1491004800000L;
    private static long EXPIRED_IN_PAST_L = 1456790400000L;

    private static int VERSION_1 = 33;
    private static int VERSION_2 = 12;

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

        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        assertFalse(db.dbIsEmpty());
    }

    @Test
    public void areRowsInsertedProperly_nonDates() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, true, VERSION_1);

        Cursor c = db.getRow(KEYID_1);
        c.moveToFirst();

        String kid = c.getString(0);
        String kname = c.getString(1);
        String tid = c.getString(2);
        String name = c.getString(3);
        int hasMedia = c.getInt(5);
        int version = c.getInt(6);
        c.close();

        assertEquals(KEYID_1, kid);
        assertEquals(KEY_NAME_1, kname);
        assertEquals(TOURID_1, tid);
        assertEquals(NAME_1, name);
        assertEquals(1, hasMedia);
        assertEquals(VERSION_1, version);
    }

    @Test
    public void areRowsInsertedProperly_dates() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, true, VERSION_1);

        Cursor c = db.getRow(KEYID_1);
        c.moveToFirst();

        long expires = c.getLong(4);
        c.close();

        assertEquals(EXPIRES_IN_FUTURE_L, expires);
    }

    @Test
    public void testRowDeletion_single() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, false, VERSION_2);

        db.deleteTour(KEYID_2);

        assertFalse(db.dbIsEmpty());

        Cursor c = db.getRow(KEYID_2);
        int count = c.getCount();
        c.close();

        assertEquals(0, count);
    }

    @Test
    public void testRowDeletion_multi() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, false, VERSION_2);

        String[] toDelete = {KEYID_1, KEYID_2};

        db.deleteTours(toDelete);
        assertTrue(db.dbIsEmpty());
    }

    @Test
    public void correctUpdateInfo() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, false, VERSION_2);

        Object[][] info = db.getTourUpdateInfo();
        assertEquals(2, info.length);

        Object[] row = info[0];
        assertEquals(KEYID_1, row[0]);
        assertEquals(TOURID_1, row[1]);
        assertEquals(VERSION_1, row[2]);

        row = info[1];
        assertEquals(KEYID_2, row[0]);
        assertEquals(TOURID_2, row[1]);
        assertEquals(VERSION_2, row[2]);
    }

    @Test
    public void testGetExpiredTours() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRED_IN_PAST, false, VERSION_2);

        String[] expiredTours = db.getExpiredTours();

        assertEquals(1, expiredTours.length);
        assertEquals(KEYID_2, expiredTours[0]);
    }

    @Test
    public void testTourExists() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        boolean tour1Exists = db.doesTourKeyNameExist(KEY_NAME_1);
        assertTrue(tour1Exists);
    }

    @Test
    public void testTourDoesNotExist() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        boolean tour2Exists = db.doesTourKeyNameExist(KEY_NAME_2);
        assertFalse(tour2Exists);
    }

    @Test
    public void testTourHasVideo() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, true, VERSION_1);

        boolean hasMedia = db.doesTourHaveMedia(KEYID_1);
        assertTrue(hasMedia);
    }

    @Test
    public void testTourDoesNotHaveVideo() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        boolean hasMedia = db.doesTourHaveMedia(KEYID_1);
        assertFalse(hasMedia);
    }

    @Test
    public void cannotPutDuplicateKeyIDs() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_1, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, true, VERSION_2);

        Cursor c = db.getRow(KEYID_1);
        int count = c.getCount();
        c.close();

        assertEquals(1, count);
    }

    @Test
    public void testVersionUpdateWorks() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        db.updateTourVersion(KEYID_1, VERSION_1 + 1);
        Object[][] info = db.getTourUpdateInfo();

        assertEquals(VERSION_1 + 1, (int) info[0][2]);
    }

    @Test
    public void testExpiryUpdateWorks() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRED_IN_PAST, false, VERSION_1);

        db.updateTourExpiry(KEYID_1, EXPIRES_IN_FUTURE_L);
        Object[][] info = db.getTourUpdateInfo();

        assertEquals(EXPIRES_IN_FUTURE_L, (long) info[0][3]);
    }

    @Test
    public void timeConverterWorks() {
        long converted;
        try {
            converted = TourDBManager.convertStampToMillis(EXPIRED_IN_PAST);
        } catch (ParseException e) {
            converted = 0;
        }

        assertEquals(EXPIRED_IN_PAST_L, converted);
    }

    @Test
    public void timeConverterWorks_not() {
        String timestamp = "2016-24-03 13:43:06";
        long converted;
        try {
            converted = TourDBManager.convertStampToMillis(timestamp);
        } catch (ParseException e) {
            converted = 0;
        }

        assertEquals(0, converted);
    }

    @After
    public void tearDown() {
        db.clearTable();
        db.close();
        db = null;
    }
}
