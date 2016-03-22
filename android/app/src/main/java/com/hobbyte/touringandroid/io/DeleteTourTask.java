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

    public DeleteTourTask(Context context, String keyID) {
        this.keyID = keyID;
        filesDir = context.getFilesDir();
    }

    @Override
    public void run() {
        File tourDir = new File(filesDir, keyID);

        if (tourDir.exists() && tourDir.isDirectory()) {
            Log.d(TAG, "About to start deleting files");

            int fileCount = 0;
            int dirCount = 0;

            /*
            Have to delete the tour JSON, plus folders containing sections, pois, images, and video
             */
            new File(tourDir, "tour").delete();
            fileCount++;

            new File(tourDir, "bundle").delete();
            fileCount++;

            new File(tourDir, "key").delete();
            fileCount++;

            String[] dirs = {"poi", "image", "video"};

            for (String d : dirs) {
                File dir = new File(tourDir, d);

                if (dir.exists() && dir.isDirectory()) {
                    for (File f : dir.listFiles()) {
                        f.delete();
                        fileCount++;
                    }

                    dir.delete();
                    dirCount++;
                }
            }

            tourDir.delete();
            dirCount++;

            Log.d(TAG, "Deleted " + fileCount + " files and " + dirCount + " folders");
        }
    }
}