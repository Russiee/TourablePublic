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

            for (File f : tourDir.listFiles()) {
                if (f.isDirectory()) {
                    deleteContentsOf(f);
                }

                f.delete();
            }

            tourDir.delete();
        }


        if (tourDir.exists()) {
            Log.w(TAG, String.format("Failed to delete all files for %s!", keyID));
        } else {
            Log.d(TAG, String.format("Deleted all files for %s", keyID));
        }
    }

    /**
     * Delete the contents of a directory.
     */
    private void deleteContentsOf(File dir) {
        for (File f : dir.listFiles()) {
            f.delete();
        }
    }
}