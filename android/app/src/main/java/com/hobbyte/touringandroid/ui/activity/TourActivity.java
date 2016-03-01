package com.hobbyte.touringandroid.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v7.widget.Toolbar;

import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.PointOfInterest;
import com.hobbyte.touringandroid.tourdata.Tour;
import com.hobbyte.touringandroid.tourdata.TourBuilder;
import com.hobbyte.touringandroid.tourdata.TourItem;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.tourdata.SubSection;
import com.hobbyte.touringandroid.ui.fragment.POIFragment;
import com.hobbyte.touringandroid.ui.fragment.SectionFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class TourActivity extends AppCompatActivity implements SectionFragment.OnFragmentInteractionListener {

    private static final String TAG = "TourActivity";

    public static final String INTENT_KEY_ID = "intentKeyID";
    public static final String INTENT_TITLE = "intentTitle";

    private static String keyID;

    private Toolbar toolbar;

    private Tour tour;
    private SubSection currentSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(INTENT_KEY_ID);
        String title = intent.getStringExtra(INTENT_TITLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        TourBuilderTask tbt = new TourBuilderTask();
        tbt.execute();
    }

    /**
     * Temporary method to see if Tour was initialised properly
     * @param section
     */
    public void printTour(SubSection section) {
        ArrayList<TourItem> contents = section.getContents();

        for (TourItem t : contents) {
            Log.d(TAG, String.format("%d %s", t.getType(), t.getTitle()));

            if (t.getType() == TourItem.TYPE_SUBSECTION) {
                printTour((SubSection) t);
            }
        }
    }

    @Override
    public void onSubSectionClicked(int position) {
        ArrayList<TourItem> contents = currentSection.getContents();

        for (TourItem t : contents) {
            Log.d(TAG, String.format("%d %s", t.getType(), t.getTitle()));
        }
        TourItem selected = contents.get(position);
        Log.d(TAG, "clicked on " + selected.getTitle());

        if (selected.getType() == TourItem.TYPE_SUBSECTION) {
            currentSection = (SubSection) contents.get(position);
            loadCurrentSection();
        } else {
            loadPointOfInterest((PointOfInterest) selected);
        }
    }

    /**
     * Takes the current section and creates a ListFragment which lists all the SubSections and
     * POIs contained in it.
     */
    private void loadCurrentSection() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        SectionFragment fragment = SectionFragment.newInstance(currentSection.getContents());

        if (currentSection == tour.getRoot()) {
            transaction.add(R.id.fragmentContainer, fragment);
        } else {
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
        }

        transaction.commit();
        toolbar.setTitle(currentSection.getTitle());
    }

    /**
     * Takes the selected POI and creates a ListFragment which shows all the posts in the POI.
     * @param poi
     */
    private void loadPointOfInterest(PointOfInterest poi) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        POIFragment fragment = POIFragment.newInstance(poi.getObjectID(), keyID);

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
        toolbar.setTitle(poi.getTitle());
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

//                printTour(currentSection);
                loadCurrentSection();
            }
        }
    }
}