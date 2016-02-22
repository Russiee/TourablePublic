package com.hobbyte.touringandroid.helpers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

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
        // TODO: get rid of this
        File file = new File(context.getFilesDir(), TOUR_DIR);
        file.mkdir();
    }

    public static JSONObject getTourJSON(String keyID) {
        // load saved JSON into JSON object and return it

        // temporary
        try {
            JSONObject tourJSON = new JSONObject();
            tourJSON.put("title", "Test Tour Title");
            tourJSON.put("description", "The best damn tour you'll ever experience. It blew my socks off");
            return tourJSON;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
