package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hobbyte.touringandroid.tourdata.PointOfInterest;
import com.hobbyte.touringandroid.ui.adapter.PointOfInterestAdapter;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.tourdata.SubSection;
import com.hobbyte.touringandroid.ui.adapter.SubSectionAdapter;

import java.util.ArrayList;

public class TourActivity extends Activity {

    //Depending on intent name, sends either arraylist of subsections, or of points of interest

    private static final String TAG = "TourActivity";
    public final static String EXTRA_MESSAGE_FINAL = "SEND_FINAL_POI";
    public final static String EXTRA_MESSAGE_SUB = "SEND_SUBSECTIONS";
    public final static String EXTRA_MESSAGE_POI = "SEND_POI";

    private ListView listView;

    private static String keyID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        loadSubSections();
    }

    /**
     * Populates a ListView with subsections given by an intent.
     */
    private void loadSubSections() {
        //Initialises final ListView
        listView = (ListView) findViewById(R.id.subsectionListView);
        Intent intent = getIntent();
        keyID = intent.getStringExtra(SummaryActivity.KEY_ID);
        //Determines whether intent contains ArrayList of Subsections or Points of Interest
        if((ArrayList<SubSection>) intent.getSerializableExtra(TourActivity.EXTRA_MESSAGE_SUB) != null) { //Checks for ArrayList of Subsections

            openSubsections((ArrayList<SubSection>) intent.getSerializableExtra(TourActivity.EXTRA_MESSAGE_SUB));
        } else {
            //Retrieves list of Points of Interest from Intent
            ArrayList<PointOfInterest> poiList = (ArrayList<PointOfInterest>) intent.getSerializableExtra(TourActivity.EXTRA_MESSAGE_POI);
            openPOIs(poiList);
        }
    }

    private void openSubsections(ArrayList<SubSection> sub) {
        SubSectionAdapter adapter = new SubSectionAdapter(this, sub); //Create Adapter for sorting list of subsections into the listView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * OnItemClickListener which, when an item is selected, either opens a new intent containing subsections within the selected subsection
             * Or creates a new intent containing Points Of Interest which are then populated
             * @param parent Adapter containing the SubSection
             * @param view ListView
             * @param position Of item within List
             * @param id of item
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                SubSection subSection = (SubSection) parent.getItemAtPosition(position);
                Intent intent = new Intent(TourActivity.this, TourActivity.class);
                if (subSection.isHasPOI()) { //If Subsection contains POIs within - Creates new intent with
                    intent.putExtra(EXTRA_MESSAGE_POI, subSection.getPOIs());
                    intent.putExtra(SummaryActivity.KEY_ID, keyID);
                } else {
                    intent.putExtra(EXTRA_MESSAGE_SUB, subSection.getListOfSub());
                    intent.putExtra(SummaryActivity.KEY_ID, keyID);
                }
                startActivity(intent);
            }
        });
    }

    private void openPOIs(ArrayList<PointOfInterest> poi) {
        System.out.println("Gets POIS");
        PointOfInterestAdapter adapter = new PointOfInterestAdapter(this, poi); //Creates adapter for sorting list into Points of Interest
        System.out.println("Gets adapter");
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                PointOfInterest poi = (PointOfInterest) parent.getItemAtPosition(position);
                Intent intent = new Intent(TourActivity.this, PointOfInterestActivity.class);
                intent.putExtra(EXTRA_MESSAGE_FINAL, poi);
                intent.putExtra(SummaryActivity.KEY_ID, keyID);
                System.out.println(keyID);
                startActivity(intent);
            }
        });
    }
}