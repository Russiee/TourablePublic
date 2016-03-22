package com.hobbyte.touringandroid.io;

import android.content.Context;
import android.util.Log;

import com.hobbyte.touringandroid.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class with static methods for doing IO with the device's internal storage.
 */
public class FileManager {
    private static final String TAG = "FileManager";

    public static final String TOUR_JSON = "tour";
    public static final String BUNDLE_JSON = "bundle";
    public static final String KEY_JSON = "key";


    /**
     * Loads a json from the tour directory, using StartActivity's application context.
     *
     * @param keyID    the keyID of the tour
     * @param filename the name of the file to be loaded
     * @return a JSON preresentaion of the file
     */
    public static JSONObject getJSON(String keyID, String filename) {
        return getJSON(App.context, keyID, filename);
    }

    /**
     * Loads a json from the tour directory.
     *
     * @param keyID    the keyID of the tour
     * @param filename the name of the file to be loaded
     * @return a JSON representation of the file
     */
    public static JSONObject getJSON(Context context, String keyID, String filename) {
        Log.d(TAG, String.format("Loading JSON from %s/%s", keyID, filename));
        try {
            File tourFolder = new File(context.getFilesDir(), keyID);
            File tourJson = new File(tourFolder, filename);

            StringBuilder text = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader(tourJson));
            String line;

            while ((line = in.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            in.close();
            return new JSONObject(text.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Creates the folders that the app will store the tour data in, using the default application
     * context.
     *
     * @param keyID the keyID of the tour. This is the unique identifier of the tour.
     */
    public static void makeTourDirectories(String keyID) {
        makeTourDirectories(App.context, keyID);
    }

    /**
     * Creates the folders that the app will store the tour data in, using a provided context.
     *
     * @param keyID the keyID of the tour. This is the unique identifier of the tour.
     */
    public static void makeTourDirectories(Context context, String keyID) {
        //...com.hobbyte.touring/files/
        File tourFolder = new File(context.getFilesDir(), keyID);
        boolean foldersCreatedSuccessfully = tourFolder.mkdir();

        //...com.hobbyte.touring/files/keyID/poi/
        File poiFolder = new File(tourFolder, "poi");
        foldersCreatedSuccessfully = poiFolder.mkdir() && foldersCreatedSuccessfully;

        //...com.hobbyte.touring/files/keyID/image/
        File imageFolder = new File(tourFolder, "image");
        foldersCreatedSuccessfully = imageFolder.mkdir() && foldersCreatedSuccessfully;

        //...com.hobbyte.touring/files/keyID/video/
        File videoFolder = new File(tourFolder, "video");
        foldersCreatedSuccessfully = videoFolder.mkdir() && foldersCreatedSuccessfully;

        //logging
        if (foldersCreatedSuccessfully)  {
            Log.i(TAG, "folders created successfully");
        } else {
            Log.e(TAG, "error creating folders");
        }
    }

    /**
     * Saves a JSONObject to the local (internal) storage, using StartActivity's application context.
     *
     * @param keyID      keyID of the tour
     * @param jsonObject the object to store
     * @param filename   the name of this JSON. BUNDLE_JSON or TOUR_JSON
     */
    public static void saveJSON(JSONObject jsonObject, String keyID, String filename) {
        saveJSON(App.context, jsonObject, keyID, filename);
    }

    /**
     * Saves a JSONObject to the local (internal) storage.
     *
     * @param keyID      keyID of the tour
     * @param jsonObject the object to store
     * @param filename   the name of this JSON. BUNDLE_JSON or TOUR_JSON
     */
    public static void saveJSON(Context context, JSONObject jsonObject, String keyID, String filename) {
        Log.d(TAG, "Saving " + filename);

        System.out.println(keyID + " " + context.getFilesDir().toString());
        File tourFolder = new File(context.getFilesDir(), keyID);
        File tourFile = new File(tourFolder, filename);

        try (FileWriter fw = new FileWriter(tourFile)){
            fw.write(jsonObject.toString());
        } catch (IOException e) {
            Log.w(TAG, "Something went wrong when saving " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Completely removes a tour from the device, deleting both its row in the local DB and all
     * downloaded files.
     *
     * @param context the calling Activity
     * @param keyID   the key ID for a specific tour
     */
    public static void removeTour(Context context, String keyID) {
        TourDBManager dbHelper = TourDBManager.getInstance(context);
        dbHelper.deleteTour(keyID);

        deleteTourFiles(context, keyID);
    }

    /**
     * Deletes all downloaded files for a particular tour.
     *
     * @param context the calling Activity
     * @param keyID the key ID for a specific tour
     */
    private static void deleteTourFiles(Context context, String keyID) {
        Log.d(TAG, "Deleting tour files for " + keyID);
        DeleteTourTask task = new DeleteTourTask(context, keyID);
        task.start();
    }
}
