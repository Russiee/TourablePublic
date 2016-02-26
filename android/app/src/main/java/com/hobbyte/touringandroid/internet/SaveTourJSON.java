package com.hobbyte.touringandroid.internet;

import android.util.Log;

import com.hobbyte.touringandroid.ui.activity.StartActivity;

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

    private File tourFolder;
    private File imageFolder;
    private File videoFolder;

    public SaveTourJSON(String keyID) {
        makeDirectories(keyID);
    }

    /**
     * Creates the folders that the app will store the tour data in
     *
     * @param keyID the keyID of the tour. This is the unique identifier of the tour.
     * @return the parent tourFolder. This name is the same as the keyID of the tour.
     */
    private void makeDirectories(String keyID) {

        //...com.hobbyte.touring/files/
        tourFolder = new File(StartActivity.getContext().getFilesDir(), keyID);
        boolean foldersCreatedSuccessfully = tourFolder.mkdir();

        //...com.hobbyte.touring/files/keyID/image/
        imageFolder = new File(tourFolder, "image");
        foldersCreatedSuccessfully = imageFolder.mkdir() && foldersCreatedSuccessfully;

        //...com.hobbyte.touring/files/keyID/video/
        videoFolder = new File(tourFolder, "video");
        foldersCreatedSuccessfully = videoFolder.mkdir() && foldersCreatedSuccessfully;

        //logging
        if (foldersCreatedSuccessfully) Log.i(TAG, "folders created successfully");
        else Log.e(TAG, "error creating folders");

    }

    /**
     * Save the tour to the device
     *
     * @param json      the tourJSON of the tour. This is the overview of the entire tour.
     * @param withVideo true if the video should be downloaded, false if only images
     */
    public void saveTour(JSONObject json, boolean withVideo) {

        WITH_VIDEO = withVideo;
        File tourFile = new File(tourFolder, "tour");
        try {

            FileWriter fw = new FileWriter(tourFile);
            fw.write(json.toString());
            fw.close();

            String tourID = json.getString("objectID");
            JSONObject bundle = ServerAPI.getJSON(tourID, ServerAPI.BUNDLE);

            if (bundle != null) {
                fw = new FileWriter(new File(tourFolder, "bundle"));
                fw.write(bundle.toString());
                fw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            e.printStackTrace();
            Log.e(TAG, json.toString());
        }
    }
}
