package com.hobbyte.touringandroid.io;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;

import java.io.File;

/**
 * Tests that the one place where tour files are deleted functions properly. This test might be
 * unnecessary as it's only use (in {@link FileManager#removeTour(Context, String)}) is tested
 * elsewhere, but we include it for completeness.
 */
@RunWith(AndroidJUnit4.class)
public class DeleteTourTaskTest {

    private static final String KEY_ID = "abc123";
    private static final String JSON_FILENAME = "fake_json_YES";
    private static final String FAKE_JSON =
            "{" +
                    "\"name\":\"Dijkstra\"," +
                    "\"profession\":\"boss\"," +
                    "\"proteges\":[\"andrew\",\"amanda\",\"jeroen\"]" +
                    "}";

    private RenamingDelegatingContext context;

    @Before
    public void setUp() throws Exception {
        context = new RenamingDelegatingContext(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_"
        );

        FileManager.makeTourDirectories(context, KEY_ID);

        JSONObject json = new JSONObject(FAKE_JSON);
        FileManager.saveJSON(context, json, KEY_ID, JSON_FILENAME);
    }

    @Test
    public void areFilesDeleted() throws Exception {
        DeleteTourTask task = new DeleteTourTask(context, KEY_ID);
        task.start();

        task.join();

        File dir = new File(context.getFilesDir(), KEY_ID);
        assertFalse(dir.exists());
    }

}
