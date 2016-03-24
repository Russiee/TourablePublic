package com.hobbyte.touringandroid.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;

/**
 * This class provides some static methods for sending requests to the server and processing
 * responses.
 */
public class ServerAPI {
    public static final String KEY_VALIDATION = "/key/verify/";
    public static final String BUNDLE = "/bundle/";
    public static final String TOUR = "/tour/";
    public static final String KEY = "/key/";
    private static final String TAG = "ServerAPI";
    private static final String BASE_URl = "https://api.tourable.org/api/v1";

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

                //read response into string
                while (line != null) {
                    jsonString.append(line);
                    line = in.readLine();
                }

                //close connection
                in.close();
                connection.disconnect();

                //convert response to json object
                JSONObject json = new JSONObject(jsonString.toString());

                //check if key has expired
                //return null if it has
                long newExpiry = TourDBManager.convertStampToMillis(json.getString("expiry"));
                if (newExpiry < System.currentTimeMillis()) {
                    Log.d(TAG, String.format("Key Expired: keyExpiry: %d, current time %d",
                            newExpiry, System.currentTimeMillis()));
                    return null;
                }

                //valid key
                String tourID = json.getJSONObject("tour").getString("objectId");

                Log.d(TAG, "Valid key: " + tourKey);
                Log.d(TAG, "Fetched tourId: " + tourID);

                return json;

            } else {
                Log.d(TAG, "Invalid key: " + tourKey);
                connection.disconnect();
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "couldn't parse expiry");
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
            connection.setConnectTimeout(2000);
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
                connection.disconnect();

                return json;

            } else {
                Log.d(TAG, "Could not find " + TYPE + " with ID = " + ID);
                connection.disconnect();
            }

        } catch (JSONException jex) {
            jex.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Log.d(TAG, "Bad connection, update aborted");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Connects to API using a given tourID and retrieves corresponding bundle as a string
     *
     * @param tourID tourID whose bundle to retrieve
     * @return Bundle string
     */
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

    /**
     * Opens a connection to a given URL
     *
     * @param urlString URL to open connection to
     * @return Open connection
     */
    public static HttpURLConnection getConnection(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
        } catch (IOException e) {
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
     * @return true if the device has an internet connection
     */
    public static boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) App.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
