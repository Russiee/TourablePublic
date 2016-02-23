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
     *
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

        Log.i(TAG, "directories made successfully");
        return tourFolder;

    }

    /**
     * Save the tour to the device
     *
     * @param json      the tourJSON of the tour. This is the overview of the entire tour.
     * @param withVideo true if the video should be downloaded, false if only images
     */
    public void saveTour(JSONObject json, boolean withVideo) {

        File tourFile = new File(tourFolder, "tour");
        try {
            FileWriter fw = new FileWriter(tourFile);
            fw.write(json.toString());
            fw.close();

            saveTopLevelTourSections(json.getJSONArray("sections"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param array the array representing the "sections" part of the tourJSON
     */
    private void saveTopLevelTourSections(JSONArray array) {

        try {

            //loop over all section objects within tourJSON
            for (int i = 0; i < array.length(); i++) {

                //this gets one of the sections of the tourJSON
                JSONObject currentSection = array.getJSONObject(i);

                //get the type of the section
                String className = currentSection.getString("className");
                String sectionId = currentSection.getString("objectId");

                //should be if API remains constant
                if (className.equals("Section")) {

                    //get json object of each top level section
                    JSONObject jsonObject = ServerAPI.getJSON(sectionId, ServerAPI.SECTION);

                    if (jsonObject != null) {
                        //save it
                        saveFile(sectionsFolder, jsonObject);
                        //save all subsections of this top level section
                        saveSubSectionsAndPois(jsonObject);
                    }

                } else {
                    //API retured a JSON structure that this code is not familiar with
                    Log.e(TAG, "section className \"" + className + "\" not expected");
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveSubSectionsAndPois(JSONObject section) {

        try {

            JSONArray subsectionsArray = section.getJSONArray("subsections");
            //if the array does actually contain objects.
            //this if statement can be removed when the api work
            if (subsectionsArray.length() != 0 && subsectionsArray.getString(0).contains(":")) {
                //loop over all subsections
                for (int i = 0; i < subsectionsArray.length(); i++) {
                    JSONObject currentSection = subsectionsArray.getJSONObject(i);
                    JSONObject sectionJSON = ServerAPI.getJSON(currentSection.getString("objectId"), ServerAPI.SECTION);
                    if (sectionJSON != null) {
                        saveFile(sectionsFolder, sectionJSON);
                        saveSubSectionsAndPois(sectionJSON);
                    }
                }
            }

            JSONArray poisArray = section.getJSONArray("pois");
            //if the array does actually contain objects.
            //this if statement can be removed when the api work
            if (poisArray.length() != 0 && poisArray.getString(0).contains(":")) {
                //loop over all pois
                for (int i = 0; i < subsectionsArray.length(); i++) {
                    JSONObject currentPoi = poisArray.getJSONObject(i);
                    JSONObject poiJSON = ServerAPI.getJSON(currentPoi.getString("objectId"), ServerAPI.POI);
                    if (poiJSON != null) {
                        saveFile(poisFolder, poiJSON);
                    }
                }
            }


        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

    }


    private void saveFile(File folderToSaveIn, JSONObject jsonToSave) {

        try {
            File file = new File(folderToSaveIn, jsonToSave.getString("objectId"));
            FileWriter fw = new FileWriter(file);
            fw.write(jsonToSave.toString());
            fw.close();
        } catch (org.json.JSONException e) {
            e.printStackTrace();
            Log.e(TAG, jsonToSave.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }


    }


}
