package com.hobbyte.touringandroid.io;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * {@link Thread} task that deletes all files and directories for a given tour.
 * <p/>
 * `execute()` takes two parameters: <ul><li>1) the File returned by context.getFilesDir()</li>
 * <li>2) the key ID</li>
 * </ul>
 * </p>
 * There is no need to use this class yourself (nor can you, outside this package) - use
 * {@link FileManager#removeTour(Context, String)} instead.
 */
class DeleteTourTask extends Thread {
    private static final String TAG = "DeleteTourTask";

    private File filesDir;
    private String keyID;
    private int count = 0;

    public DeleteTourTask(Context context, String keyID) {
        this.keyID = keyID;
        filesDir = context.getFilesDir();
    }

    @Override
    public void run() {
        File tourDir = new File(filesDir, keyID);

        if (tourDir.exists() && tourDir.isDirectory()) {
            boolean b;

            /*
            Have to delete the tour JSON, plus folders containing sections, pois, images, and video
             */
            b = new File(tourDir, "tour").delete();
            if (b) count++;

            b = new File(tourDir, "bundle").delete();
            if (b) count++;

            b = new File(tourDir, "key").delete();
            if (b) count++;

            String[] dirs = {"poi", "image", "video"};

            for (String d : dirs) {
                File dir = new File(tourDir, d);

                if (dir.exists() && dir.isDirectory()) {
                    for (File f : dir.listFiles()) {
                        b = f.delete();
                        if (b) count++;
                    }

                    b = dir.delete();
                    if (b) count++;
                }
            }

            b = tourDir.delete();
            if (b) count++;

            Log.d(TAG, "Deleted " + count + " files/folders");
        }
    }

    public int getCount() {
        return count;
    }
}