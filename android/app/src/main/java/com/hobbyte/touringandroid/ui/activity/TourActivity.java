package com.hobbyte.touringandroid.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.LinkedList;

public class TourActivity extends AppCompatActivity implements SectionFragment.OnFragmentInteractionListener {

    private static final String TAG = "TourActivity";

    public static final String INTENT_KEY_ID = "intentKeyID";
    public static final String INTENT_TITLE = "intentTitle";

    private static String keyID;

    private Toolbar toolbar;

    private RelativeLayout poiNavigation;
    private LinearLayout rightLayout;
    private LinearLayout leftLayout;
    private TextView rightPOI;
    private TextView leftPOI;

    private DrawerLayout navLayout;
    private ListView navList;
    private ArrayList<TourItem> topLevelContents;
    private TextView tourName;
    private ActionBarDrawerToggle navToggle;

    private Tour tour;
    private SubSection currentSection;
    private PointOfInterest previousPOI;
    private PointOfInterest currentPOI;

    private LinkedList<SubSection> backStack;

    private Boolean backtoSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(INTENT_KEY_ID);
        String title = intent.getStringExtra(INTENT_TITLE);

        //Created my own backstack to save the subsections previously clicked on and added to Toolbar
        backStack = new LinkedList<>();

        backtoSummary = false; //Checks whether back has been pressed at Root, and whether warning given to press back again

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.mipmap.ic_menu_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        if(backStack.size() > 1 && !backtoSummary) {
            currentSection = backStack.getLast();
            backStack.removeLast();
            backtoSummary = false;
            loadCurrentSection();
        } else if (!backtoSummary) {
            Toast.makeText(getApplicationContext(), "Please press back again to exit", Toast.LENGTH_SHORT).show();
            backtoSummary = true;
        } else if(backtoSummary){
            Intent intent = new Intent(this, SummaryActivity.class);
            intent.putExtra(SummaryActivity.KEY_ID, keyID);
            startActivity(intent);
        }
    }

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
            //If the clicked POI is not in first position in the list, sets the previous POI to the POI before the selected POI
            if((position != 0) && contents.get(position-1).getType() == TourItem.TYPE_POI) {
                previousPOI = (PointOfInterest) contents.get(position-1);
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
        backtoSummary = false;
        SectionFragment fragment = SectionFragment.newInstance(currentSection.getContents());

        if (manager.getBackStackEntryCount() < 0) {
            transaction.add(R.id.fragmentContainer, fragment);
        } else {
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
        }

        transaction.commit();
        poiNavigation.setVisibility(View.INVISIBLE);
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
        poiNavigation.setVisibility(View.VISIBLE);
        setPoiNavText(poi);
        currentPOI = poi;
        toolbar.setTitle(poi.getTitle());
    }

    private class TourBuilderTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject bundle;

        @Override
        protected Boolean doInBackground(Void... params) {
            bundle = FileManager.getJSON(getApplicationContext(), keyID, "bundle");

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
                setupNavDrawer();
                setupPoiNavigation();
                loadCurrentSection();
            }
        }
    }

    /**
     * Sets up the navigation drawer with Top-Level sections of the tour
     */
    private void setupNavDrawer() {
        navLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.left_drawer);
        tourName = (TextView) findViewById(R.id.tourNameText);

        tourName.setText(tour.getRoot().getTitle());

        topLevelContents = currentSection.getContents();

        LinearLayout home = (LinearLayout) findViewById(R.id.homeLayout);
        home.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), StartActivity.class);
               startActivity(intent);
           }
        });
        navList.setAdapter(new ArrayAdapter<>(this, R.layout.nav_drawer_item, topLevelContents));

        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                backStack.addLast(currentSection);
                currentSection = (SubSection) topLevelContents.get(position);
                loadCurrentSection();
                navLayout.closeDrawers();
            }
        });

        navToggle = new ActionBarDrawerToggle(
                this,
                navLayout,
                toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        navToggle.setDrawerIndicatorEnabled(true);
        navLayout.setDrawerListener(navToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (navToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the bottom navigation bar for traversing POIs within a section
     */
    public void setupPoiNavigation() {
        poiNavigation = (RelativeLayout) findViewById(R.id.bottomToolbar);
        leftLayout = (LinearLayout) findViewById(R.id.leftButtonLayout);
        rightLayout = (LinearLayout) findViewById(R.id.rightButtonLayout);
        rightPOI = (TextView) findViewById(R.id.rightPOI);
        leftPOI = (TextView) findViewById(R.id.leftPOI);
    }

    /**
     * Sets text for the corresponding POI (Left or Right) if such a POI exists. Otherwise makes the toolbar invisible if POI is solitary.
     * Also configures onClickListeners to load the selected Point of Interest.
     * @param poi the current Point of Interest displayed on the screen
     */
    public void setPoiNavText(PointOfInterest poi) {

        //Checks next Point of Interest, if not null sets layout to visisble and configures onClickListener
        if(poi.getNextPOI() != null) {
            rightPOI.setText(poi.getNextPOI().getTitle());
            rightLayout.setVisibility(View.VISIBLE);
            rightLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    previousPOI = currentPOI;
                    loadPointOfInterest(currentPOI.getNextPOI());
                }
            });
        } else {
            rightLayout.setVisibility(View.INVISIBLE); //Hides layout if next POI is null
        }
        //Checks previous Point of Interest, if not null and the parents of the current POI and previous POI are equal, sets layout to visible and configures listener
        if(previousPOI != null && previousPOI != poi && previousPOI.getParent() == poi.getParent()) {
            leftPOI.setText(previousPOI.getTitle());
            leftLayout.setVisibility(View.VISIBLE);
            leftLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadPointOfInterest(previousPOI);
                }
            });
        } else {
            leftLayout.setVisibility(View.INVISIBLE);
        }
        //If POI is solitary, hides the toolbar as it is not needed.
        if(rightLayout.getVisibility() == View.INVISIBLE && leftLayout.getVisibility() == View.INVISIBLE) {
            poiNavigation.setVisibility(View.INVISIBLE);
        }
    }
}