package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.PointOfInterest;
import com.hobbyte.touringandroid.tourdata.Tour;
import com.hobbyte.touringandroid.tourdata.TourBuilder;
import com.hobbyte.touringandroid.ui.adapter.PointOfInterestAdapter;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.tourdata.SubSection;
import com.hobbyte.touringandroid.ui.adapter.SubSectionAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

public class TourActivity extends Activity {

    //Depending on intent name, sends either arraylist of subsections, or of points of interest

    private static final String TAG = "TourActivity";
    public final static String EXTRA_MESSAGE_FINAL = "SEND_FINAL_POI";
    public final static String EXTRA_MESSAGE_SUB = "SEND_SUBSECTIONS";
    public final static String EXTRA_MESSAGE_POI = "SEND_POI";

    public static final String INTENT_KEY_ID = "intentKeyID";

    private ListView listView;

    private static String keyID;

    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(INTENT_KEY_ID);

        TourBuilderTask tbt = new TourBuilderTask();
        tbt.execute();

    }

    private void printTour(SubSection section) {
        Log.d(TAG, section.getTitle());
        SubSection[] sections = section.getSubSections();

        if (sections != null) {
            for (SubSection s : sections) {
                printTour(s);
            }
        }
    }



    private class TourBuilderTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject bundle;

        @Override
        protected Boolean doInBackground(Void... params) {
            bundle = FileManager.getJSON(getApplicationContext(), keyID, "bundle");
            SubSection root = null;

            if (bundle != null) {
                TourBuilder builder = new TourBuilder(bundle);
                builder.start();

                try {
                    builder.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                root = builder.getRoot();

                /*try {
                    JSONObject rootJSON = bundle.getJSONObject("root");
                    rootID = rootJSON.getString("objectId");

                    if (!rootID.equals("tour")) {
                        rootJSON = bundle.getJSONObject(rootID);
                    }

                    root = new Section(rootJSON.getString("title"), null);
                    JSONArray subsectionIDs = rootJSON.getJSONArray("subsections");

                    int length = subsectionIDs.length();
                    root.initSubSections(length);

                    for (int i = 0; i < length; ++i) {
                        parseSections(root, subsectionIDs.getString(i), i);
                    }

                } catch (JSONException je) {
                    je.printStackTrace();
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }*/
            }

            if (root != null) {
                tour = new Tour(root);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "Finished TourBuildTask");
                bundle = null;
                SubSection root = tour.getRoot();
                SubSection[] sections = root.getSubSections();

                for (SubSection s : sections) {
                    printTour(s);
                }
            }
        }

        /*private void parseSections(Section section, String subsectionID, int i) {
            try {
                JSONObject subsectionJSON = bundle.getJSONObject(subsectionID);

                String title = subsectionJSON.getString("title");
                Section subsection = new Section(title, section);
                section.addSubSection(subsection, i);

                if (subsectionJSON.has("pois")) {
                    JSONArray pois = subsectionJSON.getJSONArray("pois");
                    int length = pois.length();

                    subsection.initPOIs(length);

                    for (int j = 0; j < length; ++j) {
                        POI poi = new POI(
                                subsection,
                                pois.getJSONObject(j).getString("title"),
                                pois.getJSONObject(j).getString("objectId")
                        );
                        subsection.addPOI(poi, j);
                    }
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
        }*/
    }
}