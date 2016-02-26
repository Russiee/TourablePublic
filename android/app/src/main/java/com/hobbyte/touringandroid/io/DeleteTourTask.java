package com.hobbyte.touringandroid.io;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * AsyncTask class for deleting a tour folder
 */
public class DeleteTourTask extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "DeleteTourTask";

    @Override
    protected Void doInBackground(Object... params) {
        File tourDir = new File(
                (File) params[0],   // context.getFilesDir()
                (String) params[1]  // key ID
        );

        if (tourDir.exists() && tourDir.isDirectory()) {
            Log.d(TAG, "About to start deleting files");

            int fileCount = 0;
            int dirCount = 0;

            /*
            Have to delete the tour JSON, plus folders containing sections, pois, images, and video
             */
            new File(tourDir, "tour").delete();
            fileCount++;

            String[] dirs = {"image", "video"};

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
        return null;
    }
}