package com.hobbyte.touringandroid;

import android.test.ActivityInstrumentationTestCase2;

import com.hobbyte.touringandroid.helpers.FileManager;

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

    public void testTourDirExists() {
        File tourDir = new File(getActivity().getFilesDir(), FileManager.TOUR_DIR);

        assertEquals(true, tourDir.exists());
        assertEquals(true, tourDir.isDirectory());
    }
}
