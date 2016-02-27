package com.hobbyte.touringandroid.tourdata;

import android.content.Context;
import android.util.Log;

import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.ui.activity.SummaryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Jonathan
 */
public class TourBuilder extends Thread {
    private static final String TAG = "TourBuilder";

    private String keyID;
    SummaryActivity callBackActivity;

    public TourBuilder(String keyID, Context context, SummaryActivity callBack) {
        this.keyID = keyID;
        this.callBackActivity = callBack;
    }

    @Override
    public void run() {
        try {

            JSONObject key = ServerAPI.getJSON(keyID, ServerAPI.KEY);
            JSONObject tourJSON = key.getJSONObject("tour");
            JSONObject bundle = ServerAPI.getJSON(tourJSON.getString("objectId"), ServerAPI.BUNDLE);

            String name = bundle.getString("title");
            String description = bundle.getString("description");
            ArrayList<SubSection> subSections = getSubSectionArrayListFromJSONArray(bundle.getJSONArray("sections"));

            callBackActivity.openTourActivity(new Tour(keyID, name, description, subSections));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "Something went wring creating tour");
        }
    }

    private ArrayList<SubSection> getSubSectionArrayListFromJSONArray(JSONArray array) {
        try {

            //for all subsection objects within this json array
            ArrayList<SubSection> subSectionList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {

                //get the json object, create a java object from it and add it to the list
                JSONObject currentSubSection = array.getJSONObject(i);
                subSectionList.add(generateSubSectionObjectFromJSON(currentSubSection));
            }

            //send it back
            return subSectionList;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "error creating subsection list");
            return null;
        }
    }

    private SubSection generateSubSectionObjectFromJSON(JSONObject thisSubSection) {
        try {

            //get info about this section
            String name = thisSubSection.getString("title");
            String description = thisSubSection.getString("descriptions");
            ArrayList<PointOfInterest> poiArrayList = new ArrayList<>();

            //create a list for all subsections of this section
            ArrayList<SubSection> thisSubSectionsSubSectionsList =
                    getSubSectionArrayListFromJSONArray(thisSubSection.getJSONArray("subsections"));

            //create a list of all pois that belong in this section
            JSONArray poiJSONArray = thisSubSection.getJSONArray("pois");
            if (poiJSONArray.length() > 0) {

                for (int i = 0; i < poiJSONArray.length(); i++) {
                    //convert poi json into a java object
                    JSONObject currentPoiJSONObject = poiJSONArray.getJSONObject(i);
                    poiArrayList.add(generatePointOfInterestFromJSONObject(currentPoiJSONObject));
                }
            }

            //return the new subsection object
            return new SubSection(name, description, poiArrayList, thisSubSectionsSubSectionsList);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "error creating subsection");
            return new SubSection("Error", "There was an error creating this section", null, null);
        }

    }

    private PointOfInterest generatePointOfInterestFromJSONObject(JSONObject json) {

        //TODO create a poi javaobject from poi jsonobject

        return null;
    }


}
