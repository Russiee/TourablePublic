package com.hobbyte.touringandroid.internet;

import android.util.Log;

import com.hobbyte.touringandroid.PointOfInterest;
import com.hobbyte.touringandroid.SubSection;
import com.hobbyte.touringandroid.Tour;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class provides some static methods for sending requests to the server and processing
 * responses.
 */
public class ServerAPI {
    private static final String TAG = "ServerAPI";

    //    private static final String keyValidationURL = "https://192.168.56.1:3000/api/v1/key/verify/";
    private static final String keyValidationURL = "https://touring-api.herokuapp.com/api/v1/key/verify/";
    private static final String tourBundleURL = "https://touring-api.herokuapp.com/api/v1/bundle/";
    private static final String sectionURL = "https://touring-api.herokuapp.com/api/v1/section/";
    private static final String poiURL = "https://touring-api.herokuapp.com/api/v1/poi/";

    /**
     * Asks the server if a provided Tour Key is a real, valid key. If it is, return the
     * corresponding tour ID.
     *
     * @param tourKey a key for a tour
     * @return true if the key exists on the server
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
            Log.e(TAG, "Something went wrong verifying key!");
            return null;
        }
    }

    public static Tour allocateTourSections(String bundle) {
        try {
            URL url = new URL(tourBundleURL + bundle);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            String name;
            String description;
            ArrayList<SubSection> subList = new ArrayList<SubSection>();
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
                Log.d(TAG, "Valid bundle: " + bundle);
                JSONObject json = new JSONObject(jsonString.toString());
                name = json.getString("title");
                description = json.getString("description");
                JSONArray jsonArr = json.getJSONArray("sections");
                JSONObject jobj = jsonArr.getJSONObject(0);
                JSONArray jsonA = jobj.getJSONArray("subsections");
                for (int i = 0; i < jsonA.length(); i++) {
                    subList.add(allocateSectionPOIs(jsonA.getJSONObject(i).getString("objectId")));
                }
                return new Tour(name, description, subList);
            } else {
                Log.d(TAG, "Invalid bundle: " + bundle);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving bundle!");
            return null;
        }
    }

    public static SubSection allocateSectionPOIs(String section) {
        try {
            URL url = new URL(sectionURL + section);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            String name;
            String description;
            ArrayList<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
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
                Log.d(TAG, "Valid section: " + section);
                JSONObject json = new JSONObject(jsonString.toString());
                JSONArray jsonArr = json.getJSONArray("pois");

                name = json.getString("title");
                description = json.getString("description");

                for (int i = 0; i < jsonArr.length(); i++) {
                    poiList.add(allocatePOIs(jsonArr.getJSONObject(i).getString("objectId")));
                }
                return new SubSection(name, description, poiList);
            } else {
                Log.d(TAG, "Invalid section: " + section);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving sections!");
            return null;
        }
    }

    public static PointOfInterest allocatePOIs(String poi) {
        try {
            URL url = new URL(poiURL + poi);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            String name;
            String description;
            String header;
            String body;
            String image;
            String imageDesc;
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
                Log.d(TAG, "Valid POI: " + poi);

                JSONObject json = new JSONObject(jsonString.toString());
                JSONArray jsonArr = json.getJSONArray("post");

                name = json.getString("title");
                description = json.getString("description");
                header = "";
                body = "";
                image = "";
                imageDesc = "";

                for (int i = 0; i < jsonArr.length(); i++) {
                    if (jsonArr.getJSONObject(i).has("url")) {
                        System.out.println("has URL");
                        System.out.println(jsonArr.getJSONObject(i).toString());
                        image = jsonArr.getJSONObject(i).getString("url");
                        imageDesc = jsonArr.getJSONObject(i).getString("description");
                    } else if (jsonArr.getJSONObject(i).has("type")) {
                        if ((jsonArr.getJSONObject(i).getString("type")).equals("Header")) {
                            header = jsonArr.getJSONObject(i).getString("content");
                        }
                    } else if ((jsonArr.getJSONObject(i).getString(" type")).equals("Header")) {
                        header = jsonArr.getJSONObject(i).getString(" content");
                    } else {
//                        body = temp.getString("content");
                    }
                }

                return new PointOfInterest(name, description, header, body, image, imageDesc);
            } else {
                Log.d(TAG, "Invalid POI: " + poi);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving POIs!");
            return null;
        }
    }

}
