package com.hobbyte.touringandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hobbyte.touringandroid.helpers.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummaryActivity extends Activity {

    private static final String TAG = "SummaryActivity";

    public static final String KEY_ID = "keyID";

    private String keyID;

    private Pattern p;
    private Context context;
    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);
        context = getApplicationContext();
        p = Pattern.compile("https?:\\/\\/[\\w\\.\\/]*\\/(\\w*\\.(jpe?g|png))");
        Thread thread = new Thread() {
            @Override
            public void run() {
                    tour = allocateTourSections(FileManager.getTourJSON(keyID), context);
            }
        };
        thread.start();
        loadTourDescription();
    }

    public void openTourActivity(View v) {
        // start tour
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.EXTRA_MESSAGE_SUB, tour.getSubSections());
        intent.putExtra(SummaryActivity.KEY_ID, keyID);
        startActivity(intent);
    }

    private void loadTourDescription() {
        JSONObject tourJSON = FileManager.getTourJSON(keyID);

        TextView txtTitle = (TextView) findViewById(R.id.txtTourTitle);
        TextView txtDescription = (TextView) findViewById(R.id.txtTourDescription);

        try {
            txtTitle.setText(tourJSON.getString("title"));
            txtDescription.setText(tourJSON.getString("description"));
        } catch (Exception e) {
            e.printStackTrace();
            txtTitle.setText("Error");
            txtDescription.setText("Error");
        }
    }

    /**
     * Method takes in the Tour ID retrieved by the Key, gets the bundle from the tourId,
     * then retrieves id's of every subsection from this tour url
     * @param json
     * @return
     */
    public Tour allocateTourSections(JSONObject json, Context context) {
        try {

            String name;
            String description;
            ArrayList<SubSection> subList = new ArrayList<SubSection>();
            name = json.getString("title");
            description = json.getString("description");
            JSONArray jsonArr = json.getJSONArray("sections");
            for (int i = 0; i < jsonArr.length(); i++) {
                String objectId = jsonArr.getJSONObject(i).getString("objectId");
                JSONObject jobj = FileManager.getObjectJSON(keyID, "section", objectId, context);
                if (jobj == null) {
                    continue;
                } else {
                SubSection sub = allocateSectionPOIs(jobj);
                if (sub != null) {
                    subList.add(sub);
                } else {
                    continue;
                }
            }
            }
            Log.d(TAG, "Created tour!");
            return new Tour(keyID, name, description, subList);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with allocating Tour!");
            return null;
        }
    }

    /**
     * Method takes in a subsection id identified in the previous method,
     * opens a connection to the url of said subsection id,
     * then retrieves id's of Points of Interest and opens a method to them, creating a subsection
     * @param json
     * @return Subsection
     */
    public SubSection allocateSectionPOIs(JSONObject json) {
        try {
            String name;
            String description;

            ArrayList<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
            ArrayList<SubSection> subList = new ArrayList<SubSection>();

            Log.d(TAG, "Valid section");
            name = json.getString("title");
            description = json.getString("description");

            if(json.getJSONArray("subsections").length() == 0) {

                JSONArray jsonArr = json.getJSONArray("pois");

                System.out.println(jsonArr.toString());
                //TODO: Get rid once api fixed

                for (int i = 0; i < jsonArr.length(); i++) {

                    if (!jsonArr.getString(0).contains(":")) {
                        return null;
                    }
                    System.out.println(jsonArr.length());
                    String objectId = jsonArr.getJSONObject(i).getString("objectId");
                    System.out.println(objectId);
                    JSONObject jobj = FileManager.getObjectJSON(keyID, "poi", objectId, this);
                    if(jobj == null) {
                        continue;
                    } else {
                        PointOfInterest poi = allocatePOIs(jobj);
                        if (poi != null) {
                            poiList.add(poi);
                        } else {
                            System.out.println(objectId + "is null");
                            continue;
                        }
                    }
                }
                return new SubSection(name, description, poiList);

            } else {

                JSONArray jsonArr = json.getJSONArray("subsections");
                for (int i = 0; i < jsonArr.length(); i++) {
                    if(!jsonArr.getString(0).contains(":")) {
                        return null;
                    }
                    String objectId = jsonArr.getJSONObject(i).getString("objectId");
                    SubSection sub = allocateSectionPOIs(FileManager.getObjectJSON(keyID, "section", objectId, this));
                    if (sub != null) {
                        subList.add(sub);
                    } else {
                        continue;
                    }
                }
                return new SubSection(name, description, false, subList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving sections!");
            return null;
        }
    }

    /**
     * Retrieves information regarding the POI of the POI id passed to the constructor
     * Creates point of interest from this and passes back to caller
     * @param json JSONObject of the PointOfInterest
     * @return PointOfInterest
     */
    public PointOfInterest allocatePOIs(JSONObject json) {
        try {

            String name;
            String description;
            String header;
            String body;
            String image;
            String imageDesc;

            Log.d(TAG, "Valid POI");

            JSONArray jsonArr = json.getJSONArray("post");

            name = json.getString("title");
            description = json.getString("description");
            header = "";
            body = "";
            image = "";
            imageDesc = "";
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject temp = jsonArr.getJSONObject(i);
                if (temp.has("url")) {
                    Matcher m = p.matcher(temp.getString("url"));
                    if (m.matches()) {
                        String img = m.group(1);
                        image = img;
                        imageDesc = temp.getString("description");
                    }
                } else if (temp.has("type")) {
                    if ((temp.getString("type")).equals("Header")) {
                        header = temp.getString("content");
                    } else {
                        body = temp.getString("content");
                    }
                } else if ((temp.getString(" type")).equals("Header")) {
                    Log.d(TAG, "Used ' type'");
                    header = temp.getString(" content");
                } else {
                    body = temp.getString("content");
                }
            }
            return new PointOfInterest(name, description, header, body, image, imageDesc);

        }catch(JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong with retrieving POIs!");
            return null;
        }
    }
}
