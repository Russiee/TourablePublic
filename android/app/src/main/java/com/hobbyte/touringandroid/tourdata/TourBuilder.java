package com.hobbyte.touringandroid.tourdata;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jonathan
 */
public class TourBuilder extends Thread {
    private static final String TAG = "TourBuilder";

    private JSONObject bundle;
    private SubSection root = null;

    public TourBuilder(JSONObject bundle) {
        this.bundle = bundle;
    }

    @Override
    public void run() {
        try {
            JSONObject rootJSON = bundle.getJSONObject("root");
            String rootID = rootJSON.getString("objectId");

            if (!rootID.equals("tour")) {
                rootJSON = bundle.getJSONObject(rootID);
            }

            root = new SubSection(rootJSON.getString("title"), null);

            if (rootJSON.has("pois")) {
                addPOIs(root, rootJSON);
            }

            JSONArray subsectionIDs = rootJSON.getJSONArray("subsections");
            int length = subsectionIDs.length();
            root.initSubSections(length);

            for (int i = 0; i < length; ++i) {
                parseSections(root, subsectionIDs.getString(i), i);
            }

        } catch (JSONException je) {
            je.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseSections(SubSection section, String subsectionID, int i) {
        try {
            JSONObject subsectionJSON = bundle.getJSONObject(subsectionID);

            String title = subsectionJSON.getString("title");
            SubSection subsection = new SubSection(title, section);
            section.addSubSection(subsection, i);

            if (subsectionJSON.has("pois")) {
                addPOIs(subsection, subsectionJSON);
                /*JSONArray pois = subsectionJSON.getJSONArray("pois");
                int length = pois.length();

                subsection.initPOIs(length);

                for (int j = 0; j < length; ++j) {
                    PointOfInterest poi = new PointOfInterest(
                            subsection,
                            pois.getJSONObject(j).getString("title"),
                            pois.getJSONObject(j).getString("objectId")
                    );
                    subsection.addPOI(poi, j);
                }*/
            }

            if (subsectionJSON.has("subsections")) {
                JSONArray subSectionIDs = subsectionJSON.getJSONArray("subsections");
                int length = subSectionIDs.length();

                subsection.initSubSections(length);

                for (int j = 0; j < length; ++j) {
                    parseSections(subsection, subSectionIDs.getString(j), j);
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPOIs(SubSection section, JSONObject sectionJSON) {
        try {
            JSONArray pois = sectionJSON.getJSONArray("pois");
            int length = pois.length();

            section.initPOIs(length);

            for (int j = 0; j < length; ++j) {
                PointOfInterest poi = new PointOfInterest(
                        section,
                        pois.getJSONObject(j).getString("title"),
                        pois.getJSONObject(j).getString("objectId")
                );
                section.addPOI(poi, j);
            }
        } catch (JSONException je) {
            Log.w(TAG, "Something went wrong when adding POIs to SubSection" + section.getTitle());
        }
    }

    public SubSection getRoot() {
        return root;
    }
}
