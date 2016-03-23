package com.hobbyte.touringandroid.io;

import android.support.test.runner.AndroidJUnit4;

import com.hobbyte.touringandroid.App;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.File;

/**
 * Make sure that the expected files are downloaded when running a DownloadTourTask with and
 * without `getMedia`.
 */
@RunWith(AndroidJUnit4.class)
public class DownloadTourTaskTest {

    private static final String KEY_ID = "ZX8DHpGKxk";
    private static final String TOUR_ID = "cjWRKDygIZ";

    @Before
    public void setUp() throws Exception {
        File dir = new File(App.context.getFilesDir(), KEY_ID);

        if (dir.exists()) {
            // remove KCL-1010 in case it's already on the device
            FileManager.removeTour(App.context, KEY_ID);
            Thread.sleep(3000);
        }

        // then set up the directories again
        FileManager.makeTourDirectories(App.context, KEY_ID);
    }

    @Test
    public void downloadWithMedia() {
        DownloadTourTask task = new DownloadTourTask(null, KEY_ID, TOUR_ID, true);
        task.run();

        File baseDir = new File(App.context.getFilesDir(), KEY_ID);

        // the key JSON and tour JSON will already have been downloaded when this runs. As we have
        // skipped this, expect three directories and one file (the bundle JSON)
        assertEquals(4, baseDir.listFiles().length);

        // now check that the image and video folders are not empty
        // (don't look for a specific number of files because people have been messing with them
        // in the DB)
        File imageDir = new File(baseDir, "image");
        assertTrue(imageDir.listFiles().length > 0);

        File videoDir = new File(baseDir, "video");
        assertTrue(videoDir.listFiles().length > 0);

        // ditto for poi directory
        File poiDir = new File(baseDir, "poi");
        assertTrue(poiDir.listFiles().length > 0);
    }

    @Test
    public void downloadWithoutMedia() {
        DownloadTourTask task = new DownloadTourTask(null, KEY_ID, TOUR_ID, false);
        task.run();

        File baseDir = new File(App.context.getFilesDir(), KEY_ID);

        // the key JSON and tour JSON will already have been downloaded when this runs. As we have
        // skipped this, expect three directories and one file (the bundle JSON)
        assertEquals(4, baseDir.listFiles().length);

        // images should be downloaded
        File imageDir = new File(baseDir, "image");
        assertTrue(imageDir.listFiles().length > 0);

        // but not videos
        File videoDir = new File(baseDir, "video");
        assertTrue(videoDir.listFiles().length == 0);

        // poi directory should not be empty
        File poiDir = new File(baseDir, "poi");
        assertTrue(poiDir.listFiles().length > 0);
    }

    @After
    public void tearDown() throws Exception {
        FileManager.removeTour(App.context, KEY_ID);

        Thread.sleep(3000);
    }
}
