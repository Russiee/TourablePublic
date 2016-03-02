package com.hobbyte.touringandroid.tourdata;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Takes a JSONObject of the bundle and creates a {@link Tour} object which can be retrieved when
 * the Thread has finished executing.
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

            // if there is only one section at depth 0, BundleSaver maps the root ID to the ID for
            // that section.
            if (!rootID.equals("tour")) {
                rootJSON = bundle.getJSONObject(rootID);
            }

            JSONArray subsectionIDs = rootJSON.getJSONArray("subsections");

            // because SubSections hold their items as TourItems, we need this number to let
            // SubSections know how many of their contents are SubSections, as opposed to POIs.
            int numSubSections = subsectionIDs.length();

            root = new SubSection(null, rootJSON.getString("title"), rootID, numSubSections);

            for (int i = 0; i < numSubSections; ++i) {
                parseSections(root, subsectionIDs.getString(i));
            }

            if (rootJSON.has("pois")) {
                addPOIs(root, rootJSON);
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Recursively travel through the bundle JSON, creating {@link SubSection}s and
     * {@link PointOfInterest}s along the way.
     *
     * @param parent the parent {@link SubSection}
     * @param subsectionID the objectId of the new {@link SubSection} to create
     */
    private void parseSections(SubSection parent, String subsectionID) {
        try {
            JSONObject subsectionJSON = bundle.getJSONObject(subsectionID);

            String title = subsectionJSON.getString("title");
            SubSection subsection;

            if (subsectionJSON.has("subsections")) {
                JSONArray subSectionIDs = subsectionJSON.getJSONArray("subsections");
                int numSubSections = subSectionIDs.length();

                subsection = new SubSection(parent, title, subsectionID, numSubSections);

                for (int j = 0; j < numSubSections; ++j) {
                    parseSections(subsection, subSectionIDs.getString(j));
                }
            } else {
                subsection = new SubSection(parent, title, subsectionID, 0);
            }

            parent.addItem(subsection);

            if (subsectionJSON.has("pois")) {
                addPOIs(subsection, subsectionJSON);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Creates {@link PointOfInterest} objects from JSON and adds them to their parent section.
     */
    private void addPOIs(SubSection parent, JSONObject sectionJSON) {
        try {
            JSONArray pois = sectionJSON.getJSONArray("pois");
            int length = pois.length() - 1;

            for (int j = 0; j <= length; ++j) {
                // index of next POI in parent subsection's list of POIs
                // used in PointOfInterest.getNextPOI()
                int next = (j == length) ? -1 : j + 1;

                PointOfInterest poi = new PointOfInterest(
                        parent,
                        pois.getJSONObject(j).getString("title"),
                        pois.getJSONObject(j).getString("objectId"),
                        next
                );
                parent.addItem(poi);
            }
        } catch (JSONException je) {
            Log.w(TAG, "Something went wrong when adding POIs to SubSection" + parent.getTitle());
        }
    }

    /**
     * When the Thread has finished executing we need to be able to access the {@link Tour} that
     * was created here.
     *
     * @return a {@link Tour} instance made from the root {@link SubSection} that was created in
     * the thread.
     */
    public Tour getTour() {
        if (root == null) return null;

        return new Tour(root);
    }
}
