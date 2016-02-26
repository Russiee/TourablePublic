package com.hobbyte.touringandroid.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
     * Loads a json from the tour directory
     *
     * @param keyID    the keyID of the tour
     * @param filename the name of the file to be loaded
     * @return a JSON preresentaion of the file
     */
    public static JSONObject getJSON(String keyID, String filename) {

        try {
            File tourFolder = new File(StartActivity.getContext().getFilesDir(), keyID);
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
     * Saves a JSONObject to the local storage
     *
     * @param keyID      keyID of the tour
     * @param jsonObject the object to store
     * @param filename   the name of this JSON. BUNDLE_JSON or TOUR_JSON
     */
    public static void saveJSON(String keyID, JSONObject jsonObject, String filename) {

        File tourFolder = new File(StartActivity.getContext().getFilesDir(), keyID);
        File tourFile = new File(tourFolder, filename);

        try {

            FileWriter fw = new FileWriter(tourFile);
            fw.write(jsonObject.toString());
            fw.close();

        } catch (IOException e) {
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
     */
    public static void saveImage(String keyID, String urlString) {
        Context context = StartActivity.getContext();
        HttpURLConnection connection = null;
        Bitmap bitmap = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection != null) {
            // download image into a bitmap
            try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream())) {
                bitmap = BitmapFactory.decodeStream(bis);
            } catch (Exception e) {
                e.printStackTrace();
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
                }
            }
        }
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
    public static void deleteTourFiles(Context context, String keyID) {
        DeleteTourTask task = new DeleteTourTask();
        task.execute(context.getFilesDir(), keyID);
    }

    /**
     * Asynchronous task that deletes all files and directories for a given tour.
     * <p/>
     * `execute()` takes two parameters: <ul><li>1) the File returned by context.getFilesDir()</li>
     * <li>2) the key ID</li>
     * </ul>
     */

}
