package com.hobbyte.touringandroid.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hobbyte.touringandroid.PointOfInterest;
import com.hobbyte.touringandroid.SubSection;
import com.hobbyte.touringandroid.Tour;

import org.json.JSONArray;
import org.json.JSONException;
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
    private static final String tourURL = "https://touring-api.herokuapp.com/api/v1/tour/";
    private static final String tourBundleURL = "https://touring-api.herokuapp.com/api/v1/tour/"; // TODO: this needs to change to ...v1/bundle/
    private static final String sectionURL = "https://touring-api.herokuapp.com/api/v1/section/";
    private static final String poiURL = "https://touring-api.herokuapp.com/api/v1/poi/";

    /**
     * Asks the server if a provided Tour Key is a real, valid key. If it is, return the
     * JSON response from the server.
     *
     * @param tourKey a key for a tour
     * @return the JSON response if the key was valid; null otherwise
     */
    public static JSONObject checkKeyValidity(String tourKey) {
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
                return json;
            } else {
                Log.d(TAG, "Invalid key: " + tourKey);
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
     * @param tourID an ID corresponding to a tour
     * @return the JSON response if the tour ID exists; null otherwise
     */
    public static JSONObject getTourJSON(String tourID) {
        try {
            URL url = new URL(tourURL + tourID);
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
                JSONObject json = new JSONObject(jsonString.toString());
                Log.d(TAG, "Returning JSON for tour: " + json.getString("title"));
                return json;
            } else {
                Log.d(TAG, "Could not find tour for ID = " + tourID);
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
            URL url = new URL(tourBundleURL + tourID);
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
                return jsonString.toString();
            } else {
                Log.d(TAG, "Could not find tour for ID = " + tourID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method takes in the Tour ID retrieved by the Key, gets the bundle from the tourId,
     * then retrieves id's of every subsection from this tour url
     * @param bundle
     * @return
     */
    public static Tour allocateTourSections(String bundle) {
        try {
            URL url = new URL(tourBundleURL + bundle);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {
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
                connection.disconnect();
                Log.d(TAG, "Valid bundle: " + bundle);
                JSONObject json = new JSONObject(jsonString.toString());
                name = json.getString("title");
                description = json.getString("description");
                JSONArray jsonArr = json.getJSONArray("sections");
                //JSONObject jobj = jsonArr.getJSONObject(0);
                //JSONArray jsonA = jobj.getJSONArray("subsections");
                for (int i = 0; i < jsonArr.length(); i++) {
                    SubSection sub = allocateSectionPOIs(jsonArr.getJSONObject(i).getString("objectId"));
                    if(sub != null) {
                        subList.add(sub);
                    } else {
                        continue;
                    }
                }
                Log.d(TAG, "Fetched tourId:");
                in.close();
                return new Tour(name, description, subList);
            } else {
                connection.disconnect();
                Log.d(TAG, "Invalid bundle: " + bundle);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving bundle!");
            return null;
        }
    }

    /**
     * Method takes in a subsection id identified in the previous method,
     * opens a connection to the url of said subsection id,
     * then retrieves id's of Points of Interest and opens a method to them, creating a subsection
     * @param section
     * @return Subsection
     */
    public static SubSection allocateSectionPOIs(String section) {
        try {
            URL url = new URL(sectionURL + section);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {

                String name;
                String description;
                ArrayList<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
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
                Log.d(TAG, "Valid section: " + section);
                JSONObject json = new JSONObject(jsonString.toString());
                name = json.getString("title");
                description = json.getString("description");
                if(json.getJSONArray("subsections").length() == 0) {
                    JSONArray jsonArr = json.getJSONArray("pois");

                    for (int i = 0; i < jsonArr.length(); i++) {
                        if(!jsonArr.getString(0).contains(":")) {
                            return null;
                        }
                        PointOfInterest poi = allocatePOIs(jsonArr.getJSONObject(i).getString("objectId"));
                        if (poi != null) {
                            poiList.add(poi);
                        } else {
                            continue;
                        }
                    }
                    return new SubSection(name, description, poiList);
                } else {
                    JSONArray jsonArr = json.getJSONArray("subsections");
                    for (int i = 0; i < jsonArr.length(); i++) {
                        if(!jsonArr.getString(0).contains(":")) {
                            return null;
                        }
                        SubSection sub = allocateSectionPOIs(jsonArr.getJSONObject(i).getString("objectId"));
                        if (sub != null) {
                            subList.add(sub);
                        } else {
                            continue;
                        }
                    }
                    return new SubSection(name, description, false, subList);
                }

            } else {
                connection.disconnect();
                Log.d(TAG, "Invalid section: " + section);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving sections!");
            return null;
        }
    }

    /**
     * Retrieves information regarding the POI of the POI id passed to the constructor
     * Creates point of interest from this and passes back to caller
     * @param poi
     * @return PointOfInterest
     */
    public static PointOfInterest allocatePOIs(String poi) {
        try {
            URL url = new URL(poiURL + poi);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {
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
                connection.disconnect();
                Log.d(TAG, "Invalid POI: " + poi);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving POIs!");
            return null;
        }
    }

    /**
     * Checks if the phone currently has an internet connection, whether it's data or wifi.
     *
     * Taken from the Android
     * <a href="https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html">training guides.</a>
     *
     * @param context an Activity
     * @return true if the device has an internet connection
     */
    public static boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
