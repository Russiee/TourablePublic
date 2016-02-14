package com.hobbyte.touringandroid.internet;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class provides some static methods for sending requests to the server and processing
 * responses.
 */
public class ServerAPI {
    private static final String TAG = "ServerAPI";

    private static final String keyValidationURL = "http://192.168.56.1:3000/api/v1/key/verify/";
//    private static final String keyValidationURL = "https://touring-api.herokuapp.com/api/v1/key/verify";

    /**
     * Asks the server if a provided Tour Key is a real, valid key.
     *
     * @param tourKey a key for a tour
     * @return true if the key exists on the server
     */
    public static boolean checkKeyValidity(String tourKey) {
        try {
            URL url = new URL(keyValidationURL + tourKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();
            connection.disconnect();

            if (response == 200) {
                Log.d(TAG, "Valid key: " + tourKey);
                return true;
            } else {
                Log.d(TAG, "Invalid key: " + tourKey);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong!");
            return false;
        }
    }
}
