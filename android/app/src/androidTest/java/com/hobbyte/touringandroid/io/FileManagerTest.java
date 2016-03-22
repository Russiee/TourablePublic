package com.hobbyte.touringandroid.io;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

/**
 * Test for FileManager IO operations. Create/delete and save/load JSON.
 */
@RunWith(AndroidJUnit4.class)
public class FileManagerTest {

    private static final String KEY_ID = "qcyq1p1Yje";
    private static final String KEY_NAME = "lololololol";
    private static final String EXPIRY = "2018-01-01T00:00:00.000Z";
    private static final String JSON_FILENAME = "fake_json_YES";
    private static final String FAKE_JSON =
            "{" +
                    "\"name\":\"Dijkstra\"," +
                    "\"profession\":\"boss\"," +
                    "\"proteges\":[\"andrew\",\"amanda\",\"jeroen\"]" +
            "}";

    private RenamingDelegatingContext context;

    @Before
    public void setUp() {
        context = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_"
        );

        FileManager.makeTourDirectories(context, KEY_ID);

        try {
            JSONObject json = new JSONObject(FAKE_JSON);
            FileManager.saveJSON(context, json, KEY_ID, JSON_FILENAME);
        } catch (JSONException e) {

        }
    }

    /**
     * Make sure that FileManager properly creates the directories which will hold media for a
     * specific tour.
     */
    @Test
    public void testFolderCreation() {
        // each tour is given a directory named by its key id
        File baseDir = new File(context.getFilesDir(), KEY_ID);
        assertTrue(baseDir.exists());

        // and a folder for images
        File imageDir = new File(baseDir, "image");
        assertTrue(imageDir.exists());

        // and video
        File videoDir = new File(baseDir, "video");
        assertTrue(videoDir.exists());

        // and poi json files
        File poiDir = new File(baseDir, "poi");
        assertTrue(poiDir.exists());
    }

    /**
     * Make sure that FileManager can save a JSON file correctly.
     */
    @Test
    public void testJsonSaving() {
        File file = new File(context.getFilesDir(), String.format("%s/%s", KEY_ID, JSON_FILENAME));
        assertTrue(file.exists());
    }

    /**
     * Make sure that FileManager can load a JSONObject which we can get values from.
     */
    @Test
    public void testJsonLoading() {
        JSONObject json = FileManager.getJSON(context, KEY_ID, JSON_FILENAME);
        assertNotNull(json); // will be null if an exception was thrown inside the method

        // make sure the JSON object we loaded has the expected values
        try {
            assertEquals("Dijkstra", json.getString("name"));
            assertEquals("boss", json.getString("profession"));

            JSONArray a = json.getJSONArray("protoges");
            assertEquals(3, a.length());
            assertEquals("andrew", a.getString(0));
        } catch (JSONException e) {
            assertTrue(false); // trigger failure
        }
    }

    /**
     * Make sure that all traces of a tour are removed correctly.
     */
    @Test
    public void testTourRemoval() {
        // first we need to make a fake row in the db for our tour
        TourDBManager dbHelper = TourDBManager.getInstance(context);
        dbHelper.putRow(KEY_ID, KEY_NAME, "def", "ghi", EXPIRY, false, 1);

        // this method removes the tour from the db and deletes all associated files
        FileManager.removeTour(context, KEY_ID);

        // the row should have been removed
        assertFalse(dbHelper.doesTourKeyNameExist(KEY_NAME));

        // the files are deleted on a separate thread, so give it some time
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        File file = new File(context.getFilesDir(), KEY_ID);
        assertFalse(file.exists());

        dbHelper.clearTable();
        dbHelper.close();
    }

    @After
    public void tearDown() {
        File baseDir = new File(context.getFilesDir(), KEY_ID);

        if (baseDir.exists()) {
            for (File f : baseDir.listFiles()) {
                f.delete();
            }
        }

        baseDir.delete();
    }
}
