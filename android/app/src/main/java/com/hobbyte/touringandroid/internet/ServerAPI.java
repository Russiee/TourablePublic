package com.hobbyte.touringandroid.internet;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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

    private static final String keyValidationURL = "http://192.168.56.1:3000/api/v1/key/verify/";
//    private static final String keyValidationURL = "https://touring-api.herokuapp.com/api/v1/key/verify";

    /**
     * Asks the server if a provided Tour Key is a real, valid key. If it is, return the
     * corresponding tour ID.
     *
     * @param tourKey a key for a tour
     * @return the tour ID that corresponds to the provided key.
     */
    public static String checkKeyValidity(String tourKey) {
        try {
            URL url = new URL(keyValidationURL + tourKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            StringBuilder jsonString = new StringBuilder("");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();

            while (line != null) {
                jsonString.append(line);
                line = in.readLine();
            }

            in.close();
            connection.disconnect();


            if (response == 200) {
                Log.d(TAG, "Valid key: " + tourKey);
                JSONObject json = (JSONObject) new JSONArray(jsonString.toString()).get(0);
                String tourID = json.getJSONObject("tour").getString("objectId");
                Log.d(TAG, "Fetched tourId: " + tourID);

                return tourID;
            } else {
                Log.d(TAG, "Invalid key: " + tourKey);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong!");
            return null;
        }
    }

}
