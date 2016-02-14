package com.hobbyte.touringandroid;

import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.hobbyte.touringandroid.helpers.FileManager;
import com.hobbyte.touringandroid.helpers.TourDBManager;

import java.io.File;

/**
 * Instrumentation tests for the app's opening activity.
 */
public class StartActivityTest extends ActivityInstrumentationTestCase2<StartActivity> {

    public StartActivityTest() {
        super(StartActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // do repeated actions here
    }

    /**
     * Make sure that the holding directory for all tour media is created upon initial use.
     */
    public void testTourDirExists() {
        File tourDir = new File(getActivity().getFilesDir(), FileManager.TOUR_DIR);

        assertEquals(true, tourDir.exists());
        assertEquals(true, tourDir.isDirectory());
    }

    public void testDBExists() {
        // TODO: this test will fail as soon as there's some data in the
        // db, and will need changing
        TourDBManager dbHelper = new TourDBManager(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        boolean empty = dbHelper.dbIsEmpty(db);
        db.close();

        assertEquals(true, empty);
    }
}
