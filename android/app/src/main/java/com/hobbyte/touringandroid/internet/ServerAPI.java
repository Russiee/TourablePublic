package com.hobbyte.touringandroid.internet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class provides some static methods for sending requests to the server and processing
 * responses.
 */
public class ServerAPI {
    private static final String TAG = "ServerAPI";

    private static final String BASE_URl = "https://touring-api.herokuapp.com/api/v1";
    public static final String KEY_VALIDATION = "/key/verify/";
    public static final String BUNDLE = "/bundle/";
    public static final String TOUR = "/tour/";
    public static final String KEY = "/key/";




    /**
     * Asks the server if a provided Tour Key is a real, valid key. If it is, return the
     * JSON response from the server.
     *
     * @param tourKey a key for a tour
     * @return the JSON response if the key was valid; null otherwise
     */
    public static JSONObject checkKeyValidity(String tourKey) {
        try {
            URL url = new URL(BASE_URl + KEY_VALIDATION + tourKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {
                StringBuilder jsonString = new StringBuilder("");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = in.readLine();

                while (line != null) {
                    jsonString.append(line);
                    line = in.readLine();
                }

                in.close();
                connection.disconnect();

                Log.d(TAG, "Valid key: " + tourKey);
                JSONObject json = new JSONObject(jsonString.toString());
                String tourID = json.getJSONObject("tour").getString("objectId");
                Log.d(TAG, "Fetched tourId: " + tourID);
                return json;
            } else {
                Log.d(TAG, "Invalid key: " + tourKey);
                connection.disconnect();
                return null;
            }
        } catch (JSONException jex) {
            jex.printStackTrace();
            Log.e(TAG, "Something went wrong with the JSON!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong verifying key!");
        }
        return null;
    }

    /**
     * Fetches JSON from the server representing a tour, for a provided tour ID.
     *
     * @param ID an ID corresponding to a tour
     * @return the JSON response if the tour ID exists; null otherwise
     */
    public static JSONObject getJSON(String ID, String TYPE) {
        try {
            URL url = new URL(BASE_URl + TYPE + ID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {

                StringBuilder jsonString = new StringBuilder("");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = in.readLine();

                while (line != null) {

                    jsonString.append(line);
                    line = in.readLine();
                }

                in.close();

                JSONObject json = new JSONObject(jsonString.toString());
                Log.d(TAG, "Returning JSON for tour: " + json.getString("title"));
                connection.disconnect();

                return json;

            } else {
                Log.d(TAG, "Could not find " + TYPE + " with ID = " + ID);
                connection.disconnect();
            }

        } catch (JSONException jex) {
            jex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBundleString(String tourID) {
        try {
            URL url = new URL(BASE_URl + BUNDLE + tourID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {
                StringBuilder jsonString = new StringBuilder("");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = in.readLine();

                while (line != null) {
                    jsonString.append(line);
                    line = in.readLine();
                }

                in.close();
                connection.disconnect();

                return jsonString.toString();
            } else {
                connection.disconnect();
                Log.d(TAG, "Could not find tour for ID = " + tourID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap downloadBitmap(String urlString) {
        Log.d(TAG, "Preparing to download image at " + urlString);
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
        return bitmap;
    }

    public static HttpURLConnection getConnection(String urlString) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Checks if the phone currently has an internet connection, whether it's data or wifi.
     * <p/>
     * Taken from the Android
     * <a href="https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html">training guides.</a>
     *
     * @param context an Activity
     * @return true if the device has an internet connection
     */
    public static boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
