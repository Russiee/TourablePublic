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

    private static String EXPIRES_IN_FUTURE = "2017-04-01T00:00:00.123Z";
    private static String EXPIRED_IN_PAST = "2016-03-01T00:00:00.000Z";
    private static long EXPIRES_IN_FUTURE_L = 1491004800123L; // calculated with Epoch converter on the web
    private static long EXPIRED_IN_PAST_L = 1456790400000L; // calculated with Epoch converter on the web

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

    /**
     * Make sure that the db is empty at initialisation (and after {@link #tearDown()} is called.
     */
    @Test
    public void testEmpty() {
        assertTrue(db.dbIsEmpty());

        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        assertFalse(db.dbIsEmpty());
    }

    /**
     * Make sure that the values we pass to the method are the ones that are stored.
     */
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

    /**
     * Make sure that the date-string we pass to the method is converted and stored properly.
     */
    @Test
    public void areRowsInsertedProperly_dates() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, true, VERSION_1);

        Cursor c = db.getRow(KEYID_1);
        c.moveToFirst();

        long expires = c.getLong(4);
        c.close();

        assertEquals(EXPIRES_IN_FUTURE_L, expires);
    }

    /**
     * Make sure that the method only deletes the specified tour.
     */
    @Test
    public void testRowDeletion() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, false, VERSION_2);

        db.deleteTour(KEYID_2); // method under test

        assertFalse(db.dbIsEmpty());

        Cursor c = db.getRow(KEYID_2);
        int count = c.getCount();
        c.close();

        assertEquals(0, count);
    }

    /**
     * Make sure that the expected info is fetched by getTourUpdateInfo().
     */
    @Test
    public void correctUpdateInfo() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, false, VERSION_2);

        Object[][] info = db.getTourUpdateInfo(); // method under test
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

    /**
     * Make sure that only expired tours are fetched by the method, when only one is out of date.
     */
    @Test
    public void testGetExpiredTours_1() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRED_IN_PAST, false, VERSION_2);

        String[] expiredTours = db.getExpiredTours(); // method under test

        assertEquals(1, expiredTours.length);
        assertEquals(KEYID_2, expiredTours[0]);
    }

    /**
     * Make sure that only expired tours are fetched by the method, when both are out of date.
     */
    @Test
    public void testGetExpiredTours_2() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRED_IN_PAST, false, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRED_IN_PAST, false, VERSION_2);

        String[] expiredTours = db.getExpiredTours();

        assertEquals(2, expiredTours.length);
    }

    /**
     * Make sure that the method correctly identifies the tour as existing.
     */
    @Test
    public void testTourExists() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        boolean tour1Exists = db.doesTourKeyNameExist(KEY_NAME_1);
        assertTrue(tour1Exists);
    }

    /**
     * Make sure that the method correctly identifies the tour as not existing.
     */
    @Test
    public void testTourDoesNotExist() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        boolean tour2Exists = db.doesTourKeyNameExist(KEY_NAME_2);
        assertFalse(tour2Exists);
    }

    /**
     * Make sure that boolean conversion works properly on `hasVideo` field.
     */
    @Test
    public void testTourHasVideo() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, true, VERSION_1);
        db.putRow(KEYID_2, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, false, VERSION_2);

        boolean hasMedia_1 = db.doesTourHaveMedia(KEYID_1);
        boolean hasMedia_2 = db.doesTourHaveMedia(KEYID_2);

        assertTrue(hasMedia_1);
        assertFalse(hasMedia_2);
    }

    /**
     * Make sure that trying to insert a row with an already existing keyID is ignored by the DB.
     */
    @Test
    public void cannotPutDuplicateKeyIDs() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        // this should internally throw an SQLiteConstraintException error, because keyID is
        // a primary key attribute. This error does not affect the app's runtime and does
        // not enter the new row into the table.
        db.putRow(KEYID_1, TOURID_2, KEY_NAME_2, NAME_2, EXPIRES_IN_FUTURE, true, VERSION_2);

        Cursor c = db.getRow(KEYID_1);
        int count = c.getCount();
        c.moveToFirst();
        String tourID = c.getString(2); // should be the tour id from the first row that was entered
        c.close();

        assertEquals(1, count);
        assertEquals(TOURID_1, tourID);
    }

    /**
     * Make sure that we can increment a tour's version number correctly.
     */
    @Test
    public void testVersionUpdateWorks() {
        String newName = "Sanders 2016";
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRES_IN_FUTURE, false, VERSION_1);

        db.updateRow(KEYID_1, newName, true, VERSION_1 + 1); // method under test
        Cursor c = db.getRow(KEYID_1);
        c.moveToFirst();
        String name = c.getString(3);
        int version = c.getInt(6);
        c.close();

        assertEquals(VERSION_1 + 1, version);
        assertEquals(newName, name);
        assertTrue(db.doesTourHaveMedia(KEYID_1));
    }

    /**
     * Make sure we can change a tour's expiry date correctly.
     */
    @Test
    public void testExpiryUpdateWorks() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRED_IN_PAST, false, VERSION_1);

        db.updateTourExpiry(KEYID_1, EXPIRES_IN_FUTURE_L); // method under test
        Object[][] info = db.getTourUpdateInfo();

        assertEquals(EXPIRES_IN_FUTURE_L, (long) info[0][3]);
    }

    /**
     * Make sure that we can correctly change whether or not a tour has an update available
     */
    @Test
    public void testUpdateFlagSetting() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRED_IN_PAST, false, VERSION_1);

        assertFalse(db.doesTourHaveUpdate(KEYID_1));

        db.flagTourUpdate(KEYID_1, true);
        assertTrue(db.doesTourHaveUpdate(KEYID_1));

        db.flagTourUpdate(KEYID_1, false);
        assertFalse(db.doesTourHaveUpdate(KEYID_1));
    }

    /**
     * Make sure that date conversion works internally.
     */
    @Test
    public void testGetExpiryDate() {
        db.putRow(KEYID_1, TOURID_1, KEY_NAME_1, NAME_1, EXPIRED_IN_PAST, false, VERSION_1);

        assertEquals(EXPIRED_IN_PAST_L, db.getExpiryDate(KEYID_1));
    }

    /**
     * A more explicit test of the method that actually does all of the string -> long time
     * conversion.
     */
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

    /**
     * Make sure that the method only accepts a properly formatted timestamp string.
     */
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

    /**
     * Start each test with a fresh database.
     */
    @After
    public void tearDown() {
        db.clearTable();
        db.close();
        db = null;
    }
}
