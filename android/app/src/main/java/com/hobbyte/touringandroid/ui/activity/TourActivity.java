package com.hobbyte.touringandroid.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ListView;
//import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;

import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.PointOfInterest;
import com.hobbyte.touringandroid.tourdata.Tour;
import com.hobbyte.touringandroid.tourdata.TourBuilder;
import com.hobbyte.touringandroid.ui.adapter.PointOfInterestAdapter;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.tourdata.SubSection;
import com.hobbyte.touringandroid.ui.adapter.SubSectionAdapter;
import com.hobbyte.touringandroid.ui.fragment.SectionFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class TourActivity extends Activity implements SectionFragment.OnFragmentInteractionListener {

    //Depending on intent name, sends either arraylist of subsections, or of points of interest

    private static final String TAG = "TourActivity";
    public final static String EXTRA_MESSAGE_FINAL = "SEND_FINAL_POI";
    public final static String EXTRA_MESSAGE_SUB = "SEND_SUBSECTIONS";
    public final static String EXTRA_MESSAGE_POI = "SEND_POI";

    public static final String INTENT_KEY_ID = "intentKeyID";
    public static final String INTENT_TITLE = "intentTitle";

    private ListView listView;

    private static String keyID;

    private Tour tour;
    private SubSection currentSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(INTENT_KEY_ID);
        String title = intent.getStringExtra(INTENT_TITLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        TourBuilderTask tbt = new TourBuilderTask();
        tbt.execute();
    }



    /**
     * Temporary method to chack that Tour was loaded properly
     * @param section
     */
    private void printTour(SubSection section) {
        Log.d(TAG, section.getTitle());
        SubSection[] sections = section.getSubSections();

        if (sections != null) {
            for (SubSection s : sections) {
                printTour(s);
            }
        }
    }

    private void loadRootFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SectionFragment fragment = SectionFragment.newInstance(tour.getRoot().getSubSections());
        fragmentTransaction.add(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSubSectionClicked(int position) {
        SubSection[] subsections = currentSection.getSubSections();
        Log.d(TAG, "clicked on " + subsections[position].getTitle());
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
            }

            if (root != null) {
                tour = new Tour(root);
                currentSection = tour.getRoot();
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

                loadRootFragment();
            }
        }
    }
}