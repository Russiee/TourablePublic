package com.hobbyte.touringandroid;

import android.util.Log;

import com.hobbyte.touringandroid.internet.ServerAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jonathan Burton
 */
public class SaveTourJSON {
    public static String TAG = "SaveTourJSON";

    public static boolean JUST_IMAGES = false;
    public static boolean WITH_VIDEO = true;

    private String keyID;

    private File tourFolder;
    private File sectionsFolder;
    private File poisFolder;
    private File imageFolder;
    private File videoFolder;


    public SaveTourJSON(String keyID) {
        this.keyID = keyID;
        this.tourFolder = makeDirectories(keyID);
    }

    /**
     * Creates the folders that the app will store the tour data in
     * @param keyID the keyID of the tour. This is the unique identifier of the tour.
     * @return the parent tourFolder. This name is the same as the keyID of the tour.
     */
    private File makeDirectories(String keyID) {

        //create folder in ...com.hobbyte.touring/files/
        tourFolder = new File(StartActivity.getContext().getFilesDir(), keyID);
        tourFolder.mkdir();

        //...com.hobbyte.touring/files/keyID/section
        sectionsFolder = new File(tourFolder, "sections");
        sectionsFolder.mkdir();

        //...com.hobbyte.touring/files/keyID/pois
        poisFolder = new File(tourFolder, "pois");
        poisFolder.mkdir();

        //...com.hobbyte.touring/files/keyID/image
        imageFolder = new File(tourFolder, "image");
        imageFolder.mkdir();

        //...com.hobbyte.touring/files/keyID/video
        videoFolder = new File(tourFolder, "video");
        videoFolder.mkdir();

        return tourFolder;

    }

    /**
     * Save the tour to the device
     * @param json the tourJSON of the tour. This is the overview of the entire tour.
     * @param withVideo true if the video should be downloaded, false if only images
     */
    public void saveTour(JSONObject json, boolean withVideo) {

        File tourFile = new File(tourFolder, "tour");
        try {
            FileWriter fw = new FileWriter(tourFile);
            fw.write(json.toString());
            fw.close();

            recurseIntoAPI(json.getJSONArray("sections"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param array
     */
    private void recurseIntoAPI(JSONArray array) {

        try {

            Log.i(TAG,"length: " +  array.length());
            for (int i = 0; i < array.length(); i++) {
                //this gets the current 3 value sections of the tourJSON
                JSONObject currentSection = array.getJSONObject(i);
                Log.i(TAG, currentSection.toString());

                //get the type of the section
                String type = currentSection.getString("className");
                String sectionId = currentSection.getString("objectId");

                if (type.equals("Section")) {

                    JSONObject jsonObject = ServerAPI.getJSON(sectionId, ServerAPI.SECTION);
                    saveFile(sectionsFolder, jsonObject);

                    //recurse

                } else if (type.equals("POI")) {

                    JSONObject jsonObject = ServerAPI.getJSON(sectionId, ServerAPI.POI);
                    saveFile(poisFolder, jsonObject);

                } else {
                    Log.e(TAG, "section className \"" + type + "\" not expected");
                }

            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();

        }
    }

    private void saveFile(File folderToSaveIn, JSONObject jsonToSave) {

        try {
            File file = new File(folderToSaveIn, jsonToSave.getString("objectID"));
            FileWriter fw = new FileWriter(file);
            fw.write(jsonToSave.toString());
        }
        catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }


}
