package com.hobbyte.touringandroid.io;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Extracts all information from the bundle that is needed to create a tour structure. The bundle
 * is saved in an altered format, where each key is a section objectId. The keys point to a
 * JSONObject which holds the section title, description, and JSONArrays for any subsections and
 * POIs where they exist.
 * <p/>
 * This is the format of the bundle that is saved on the device:
 * <p/>
 * {
 * "sectionID_A": {
 * "title": "hello",
 * "description": "I say hello",
 * "subsections": [
 * {
 * "objectId": "sectionID_B",
 * "title": "world"
 * }, ...
 * ],
 * "pois": [
 * {
 * "objectId": "poiID_A",
 * "title": "Foo"
 * }, ...
 * },
 * "sectionID_B": {
 * "title": "world",
 * "description": "Goodbye",
 * "subsections": [
 * ....
 * ],
 * ...
 * },
 * ...
 * }
 * <p/>
 * Additionally a "root" key is stored, which determines what is shown when the user first enters a
 * Tour Activity.
 * <p/>
 * Note that the POIs are not saved in this bundle. The JSON for each POI is saved
 * in an individual file, that is named according to the POI's objectId.
 */
public class BundleSaver extends Thread {

    private static final String TAG = "BundleSaver";

    private String bundle;
    private String keyID;
    private JSONObject json;
    private Context context;

    private HashSet<String> imageURLs = new HashSet<>();
    private HashSet<String> videoURLs = new HashSet<>();

    private Pattern whitespace;

    public BundleSaver(Context c, String bundleString, String keyid) {
        context = c;
        bundle = bundleString;
        keyID = keyid;
        whitespace = Pattern.compile("\\s+");
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "Saving bundle for " + keyID);

            JSONObject bundleTemp = new JSONObject(bundle);
            json = new JSONObject();

            JSONArray sections = bundleTemp.getJSONArray("sections");
            int depthZeroCount = bundleTemp.getJSONArray("sections").length();

            JSONObject tourRoot = new JSONObject();
            JSONArray topSections = new JSONArray();

            for (int i = 0; i < depthZeroCount; ++i) {
                String objectId = sections.getJSONObject(i).getString("objectId");
                topSections.put(i, objectId);
            }

            tourRoot.put("subsections", topSections);
            tourRoot.put("title", bundleTemp.getString("title"));
            tourRoot.put("description", bundleTemp.getString("description"));

            json.put("root", tourRoot);

            // don't need bundleTemp or the String anymore; set them to null to conserve memory
            bundleTemp = null;
            bundle = null;

            // for all sections and POIs in the bundle, extract needed info and store it in `json`
            for (int i = 0; i < sections.length(); ++i) {
                buildBundle(sections.getJSONObject(i));
            }

            FileManager.saveJSON(context, json, keyID, FileManager.BUNDLE_JSON);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * @return all image URLs in the bundle JSON
     */
    public HashSet<String> getImageURLs() {
        return imageURLs;
    }

    /**
     * @return all video URLs in the bundle JSON
     */
    public HashSet<String> getVideoURLs() {
        return videoURLs;
    }

    /**
     * Recursively travel through a section of the bundle, extracting the title, description,
     * subsections, and POIs.
     *
     * @param section a JSONObject of one section in bundle.sections
     */
    private void buildBundle(JSONObject section) {
        try {
            Log.d(TAG, "Processing section: " + section.getString("title"));

            JSONObject sectionInfo = new JSONObject();
            sectionInfo.put("title", section.getString("title"));
            sectionInfo.put("description", section.getString("description"));

            boolean hasSubsections = false;

            // store IDs and titles of each POI in this section
            if (section.has("pois")) {
                JSONArray pois = extractPOI(section);
                sectionInfo.put("pois", pois);
            }

            // store IDs and titles of each subsection in this section
            if (section.has("subsections")) {
                hasSubsections = true;
                JSONArray subsections = extractSubsections(section);
                sectionInfo.put("subsections", subsections);
            }

            json.put(section.getString("objectId"), sectionInfo);

            if (hasSubsections) {
                JSONArray subsections = section.getJSONArray("subsections");

                for (int i = 0; i < subsections.length(); ++i) {
                    buildBundle(subsections.getJSONObject(i));
                }
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Extract the objectId's and titles from a section's subsections.
     *
     * @param section a JSONObject of one section in bundle.sections
     * @return a JSONArray containing JSONObjects for each subsection in this section
     */
    private JSONArray extractSubsections(JSONObject section) {
        try {
            JSONArray bundleArray = section.getJSONArray("subsections");
            JSONArray array = new JSONArray();

            for (int i = 0; i < bundleArray.length(); ++i) {
                JSONObject subsection = bundleArray.getJSONObject(i);

                String objectId = subsection.getString("objectId");
                array.put(i, objectId);
            }

            return array;
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return null;
    }

    /**
     * Extract the objectId's and titles from a sections POIs, and save each POI on the device.
     *
     * @param section a JSONObject of one section in bundle.sections
     * @return a JSONArray containing JSONObjects for each POI in this section
     */
    private JSONArray extractPOI(JSONObject section) {
        try {
            JSONArray bundleArray = section.getJSONArray("pois");
            JSONArray array = new JSONArray();

            for (int i = 0; i < bundleArray.length(); ++i) {
                JSONObject poi = bundleArray.getJSONObject(i);
                JSONObject o = new JSONObject();
                String id = poi.getString("objectId");

                o.put("objectId", id);
                o.put("title", poi.getString("title"));
                array.put(i, o);

                addURLs(poi.getJSONArray("post"));
                FileManager.saveJSON(context, poi, keyID, String.format("poi/%s", id));
            }

            return array;
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return null;
    }

    /**
     * Extracts the URLs from the "post" segment of a POI, and adds them to a Set to be used later
     * in DownloadTourTask.
     */
    private void addURLs(JSONArray array) {
        for (int i = 0; i < array.length(); ++i) {
            try {
                JSONObject postItem = array.getJSONObject(i);

                if (postItem.has("url")) {
                    String type = postItem.getString("type");
                    String url;

                    try {
                        url = URLDecoder.decode(postItem.getString("url"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, e.getMessage());
                        url = postItem.getString("url");
                    }

                    url = whitespace.matcher(url).replaceAll("+");
                    postItem.put("url", url);
//                    String url = postItem.getString("url");

                    if (type.equals("image")) {
                        imageURLs.add(url);
                    } else if (type.equals("video")) {
                        videoURLs.add(url);
                    }
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }

        }
    }
}
