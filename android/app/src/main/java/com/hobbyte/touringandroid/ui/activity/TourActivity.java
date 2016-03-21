package com.hobbyte.touringandroid.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.PointOfInterest;
import com.hobbyte.touringandroid.tourdata.SubSection;
import com.hobbyte.touringandroid.tourdata.Tour;
import com.hobbyte.touringandroid.tourdata.TourBuilder;
import com.hobbyte.touringandroid.tourdata.TourItem;
import com.hobbyte.touringandroid.ui.fragment.POIFragment;
import com.hobbyte.touringandroid.ui.fragment.SectionFragment;
import com.hobbyte.touringandroid.ui.util.ImageCache;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class TourActivity extends AppCompatActivity implements SectionFragment.OnFragmentInteractionListener {

    private static final String TAG = "TourActivity";

    public Context context = this;
    public static final String INTENT_KEY_ID = "intentKeyID";
    public static final String INTENT_TITLE = "intentTitle";

    private static String keyID;

    private TextView sectionDescription;
    private Toolbar toolbar;

    private Tour tour;
    private SubSection currentSection;
    public PointOfInterest previousPOI;
    public PointOfInterest currentPOI;

    private LinkedList<SubSection> backStack;

    private Boolean backToSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        App app = (App) this.getApplicationContext();
        app.setCurrentActivity(this);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(INTENT_KEY_ID);
        String title = intent.getStringExtra(INTENT_TITLE);

        //Created my own backstack to save the subsections previously clicked on and added to Toolbar
        backStack = new LinkedList<>();

        backToSummary = false; //Checks whether back has been pressed at Root, and whether warning given to press back again

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        sectionDescription = (TextView) findViewById(R.id.sectionDescription);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSummary = true;
                onBackPressed();
                backToSummary = false;
            }
        });
        TourBuilderTask tbt = new TourBuilderTask();
        tbt.execute();
    }

    /**
     * Listens for a back button press and displays previously opened fragment
     * Also listens for a back button press at the Root Fragment and signals
     * that another back button press will return to summary activity
     */
    @Override
    public void onBackPressed() {
        if (backStack.size() > 1) {
            currentSection = backStack.getLast();
            backStack.removeLast();
            backToSummary = false;
            loadCurrentSection();
        } else if (!backToSummary) {
            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            backToSummary = true;
        } else if (backToSummary) {
            super.onBackPressed();
            this.finish();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop Called");
        ImageCache.getInstance().clearCache();
        super.onStop();
    }

    /**
     * Opens appropriate Section or PointOfInterest depending on the item clicked on
     *
     * @param position position of the item clicked on
     */
    @Override
    public void onSubSectionClicked(int position) {
        ArrayList<TourItem> contents = currentSection.getContents();
        backStack.addLast(currentSection);
        for (TourItem t : contents) {
            Log.d(TAG, String.format("%d %s", t.getType(), t.getTitle()));
        }
        TourItem selected = contents.get(position);
        Log.d(TAG, "clicked on " + selected.getTitle());

        if (selected.getType() == TourItem.TYPE_SUBSECTION) {
            currentSection = (SubSection) contents.get(position);
            loadCurrentSection();
        } else {
            // if the clicked POI is not in first position in the list, sets the previous POI to
            // the POI before the selected POI
            if ((position != 0) && contents.get(position - 1).getType() == TourItem.TYPE_POI) {
                previousPOI = (PointOfInterest) contents.get(position - 1);
            }
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
        backToSummary = false;
        SectionFragment fragment = SectionFragment.newInstance(currentSection.getContents());

        if (manager.getBackStackEntryCount() < 0) {
            transaction.add(R.id.fragmentContainer, fragment);
        } else {
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
        }

        transaction.commit();
        toolbar.setTitle(currentSection.getTitle());
        sectionDescription.setVisibility(View.VISIBLE);
        sectionDescription.setText(currentSection.getDescription());
    }

    /**
     * Takes the selected POI and creates a ListFragment which shows all the posts in the POI.
     *
     * @param poi
     */
    public void loadPointOfInterest(PointOfInterest poi) {
        currentPOI = poi;
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        POIFragment fragment = POIFragment.newInstance(poi.getObjectID(), keyID, previousPOI, currentPOI);

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
        sectionDescription.setVisibility(View.GONE);
        toolbar.setTitle(poi.getTitle());
    }

    /**
     * Prepares and builds the tour from the given keyID
     */
    private class TourBuilderTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject bundle;

        @Override
        protected Boolean doInBackground(Void... params) {
            bundle = FileManager.getJSON(keyID, "bundle");

            if (bundle != null) {
                TourBuilder builder = new TourBuilder(bundle);
                builder.start();

                try {
                    builder.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                tour = builder.getTour();
                currentSection = tour.getRoot();
                backStack.addLast(currentSection);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "Finished TourBuildTask");
                bundle = null;

                tour.printTour(tour.getRoot(), 0);
                loadCurrentSection();
            }
        }
    }

}