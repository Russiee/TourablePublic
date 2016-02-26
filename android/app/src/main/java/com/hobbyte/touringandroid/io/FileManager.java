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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class with static methods for doing IO with the device's internal storage.
 */
public class FileManager {

    public static final String IMG_NAME = "https?:\\/\\/[\\w\\.\\/]*\\/(\\w*\\.(jpe?g|png))";

    /**
     * Loads a saved tour JSON file for a given tour.
     *
     * @param keyID the tour key used to download the tour
     * @return a JSONObject of the top-level tour
     */
    public static JSONObject getTourJSON(String keyID) {
        // load saved JSON into JSON object and return it

        // temporary
        try {
            File tourFolder = new File(StartActivity.getContext().getFilesDir(), keyID);
            File tourJson = new File(tourFolder, "tour");

            StringBuilder text = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader(tourJson));
            String line;

            while((line = in.readLine()) != null) {
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
     * Loads a saved JSON File for a given Tours Section or POI
     * @param keyID the tour key used to download the tour
     * @param object the level of section, Section or Point Of Interest
     * @param objectId The id of the object whose JSON to retrieve
     * @param context The context of the calling activity
     * @return JSONObject for the Level of Section required
     */
    public static JSONObject getObjectJSON(String keyID, String object, String objectId, Context context) {

        try {
            File tourFolder = new File(context.getFilesDir(), keyID);
            File iDFolder = new File(tourFolder, object);
            File objectJson = new File(iDFolder, objectId);

            if(!objectJson.exists()) {
                return null;
            }

            StringBuilder text = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader(objectJson));
            String line;

            while((line = in.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            in.close();

            return new JSONObject(text.toString());

        } catch (IOException e) {
            System.out.println("Error opening file...");
            e.printStackTrace();
            return null;
        } catch (JSONException jex) {
            jex.printStackTrace();
            return null;
        }
    }
    /**
     * Saves an image given by a URL to the device.
     * <p>
     * This method MUST NOT be called from within the main thread.
     *
     * @param context the calling Activity
     * @param keyID a tour key ID
     * @param urlString a URL to an image file
     */
    public static void saveImage(Context context, String keyID, String urlString) {
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
     * Deletes all files associated with a tour.
     *
     * @param context the calling Activity
     * @param keyID the key ID for a specific tour
     */
    public static void deleteTourFiles(Context context, String keyID) {
        DeleteTourTask task = new DeleteTourTask();
        task.execute(context.getFilesDir(), keyID);
    }
}
