package com.hobbyte.touringandroid.helpers;

import android.content.Context;

import java.io.File;

/**
 * A class with several static methods for managing the app's internal storage.
 */
public class FileManager {

    public static final String TOUR_DIR = "tourData";

    /**
     * This is an action which only has to be performed after a fresh install of the app.
     * It creates a directory in which all tour media will be stored.
     *
     * @param context the starting activity
     */
    public static void makeTourDir(Context context) {
        File file = new File(context.getFilesDir(), TOUR_DIR);
        file.mkdir();
    }
}
