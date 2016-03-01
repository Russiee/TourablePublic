package com.hobbyte.touringandroid.io;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.hobbyte.touringandroid.ui.activity.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class with static methods for doing IO with the device's internal storage.
 */
public class FileManager {
    private static final String TAG = "FileManager";

    public static final String IMG_NAME = "https?:\\/\\/[\\w\\.\\/]*\\/(\\w*\\.(jpe?g|png))";

    public static final String TOUR_JSON = "tour";
    public static final String BUNDLE_JSON = "bundle";


    /**
     * Loads a json from the tour directory, using StartActivity's application context.
     *
     * @param keyID    the keyID of the tour
     * @param filename the name of the file to be loaded
     * @return a JSON preresentaion of the file
     */
    public static JSONObject getJSON(String keyID, String filename) {
        return getJSON(StartActivity.getContext(), keyID, filename);
    }

    /**
     * Loads a json from the tour directory.
     *
     * @param keyID    the keyID of the tour
     * @param filename the name of the file to be loaded
     * @return a JSON preresentaion of the file
     */
    public static JSONObject getJSON(Context context, String keyID, String filename) {
        Log.d(TAG, "Loading JSON from " + filename);
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
     * Creates the folders that the app will store the tour data in
     *
     * @param keyID the keyID of the tour. This is the unique identifier of the tour.
     */
    public static void makeTourDirectories(Context context, String keyID) {

        //...com.hobbyte.touring/files/
        File tourFolder = new File(StartActivity.getContext().getFilesDir(), keyID);
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
        if (foldersCreatedSuccessfully) Log.i(TAG, "folders created successfully");
        else Log.e(TAG, "error creating folders");

    }

    /**
     * Saves a JSONObject to the local (internal) storage, using StartActivity's application context.
     *
     * @param keyID      keyID of the tour
     * @param jsonObject the object to store
     * @param filename   the name of this JSON. BUNDLE_JSON or TOUR_JSON
     */
    public static void saveJSON(JSONObject jsonObject, String keyID, String filename) {
        saveJSON(StartActivity.getContext(), jsonObject, keyID, filename);
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
     * Saves an image given by a URL to the device.
     * <p/>
     * This method MUST NOT be called from within the main thread.
     *
     * @param keyID     a tour key ID
     * @param urlString a URL to an image file
     * @return true if the file was saved successfully
     */
    public static boolean saveImage(Context context, String keyID, String urlString) {
        Log.d(TAG, "Preparing to download image at " + urlString);
        HttpURLConnection connection = null;
        Bitmap bitmap = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (connection != null) {
            // download image into a bitmap
            try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream())) {
                bitmap = BitmapFactory.decodeStream(bis);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                connection.disconnect();
            }
        }

        if (bitmap != null) {
            // extract image file name and save it on device
            Matcher m = Pattern.compile(IMG_NAME).matcher(urlString);

            if (m.matches()) {
                String img = m.group(1);
                File file = new File(context.getFilesDir(), String.format("%s/image/%s", keyID, img));

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Saves an video given by a URL to the device.
     * <p/>
     * This method MUST NOT be called from within the main thread.
     *
     * @param keyID     a tour key ID
     * @param urlString a URL to an video file
     */
    public static void saveVideo(String keyID, String urlString) {
        //TODO
    }

    /**
     * Deletes all files associated with a tour.
     *
     * @param context the calling Activity
     * @param keyID   the key ID for a specific tour
     */
    public static void removeTour(Context context, String keyID) {
        TourDBManager dbHelper = TourDBManager.getInstance(context);
        dbHelper.deleteTour(keyID);

        DeleteTourTask task = new DeleteTourTask();
        task.execute(context.getFilesDir(), keyID);
    }
}
