package com.hobbyte.touringandroid.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class with static methods for doing IO with the device's internal storage.
 */
public class FileManager {

    public static final String TOUR_DIR = "tourData";
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
            JSONObject tourJSON = new JSONObject();
            tourJSON.put("title", "Test Tour Title");
            tourJSON.put("description", "The best damn tour you'll ever experience. It blew my socks off");
            return tourJSON;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves an image given by a URL to the device.
     *
     * @param context the calling Activity
     * @param keyID a tour key ID
     * @param urlString a URL to an image file
     */
    public static void saveImage(Context context, String keyID, String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // download image into a bitmap
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            Bitmap bitmap = BitmapFactory.decodeStream(bis);

            connection.disconnect();

            // extract image file name and save it on device
            Matcher m = Pattern.compile(IMG_NAME).matcher(urlString);

            if (m.matches()) {
                String img = m.group(1);
                File file = new File(context.getFilesDir(), String.format("%s/images/%s", keyID, img));
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
