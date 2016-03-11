package com.hobbyte.touringandroid.io;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Created by max on 09/03/16.
 */
@RunWith(AndroidJUnit4.class)
public class DBTest {
//public class DBTest extends AndroidTestCase {

    private TourDBManager db;

    private static String KEYID_1 = "qwerty";
    private static String KEYID_2 = "azerty";

    private static String TOURID_1 = "1kdlNd7";
    private static String TOURID_2 = "pLd3B8d";

    private static String NAME_1 = "iLuvTestsTour";
    private static String NAME_2 = "iHateTestsTour";

    private static String CREATED_1 = "2016-02-24T12:32:06.952Z";
    private static String CREATED_2 = "2015-12-24T23:59:59.123Z";
    private static long CREATED_1_long = 1456334319855L;

    private static String UPDATED_1 = "2016-02-25T12:32:06.952Z";
    private static String UPDATED_2 = "2015-12-25T12:32:06.952Z";

    @Before
    public void setUp() {
        RenamingDelegatingContext context = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_"
        );
        db = TourDBManager.getInstance(context);

    }

    @Test
    public void testEmpty() {
        assertTrue(db.dbIsEmpty());

        db.putRow(KEYID_1, TOURID_1, "name", CREATED_1, UPDATED_1, UPDATED_1, false);
        assertFalse(db.dbIsEmpty());
    }


    @After
    public void tearDown() {
        db.close();
        db = null;
    }
}
