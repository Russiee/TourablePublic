package com.hobbyte.touringandroid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class TourActivity extends Activity {

    //Depending on intent name, sends either arraylist of subsections, or of points of interest

    public final static String EXTRA_MESSAGE_FINAL = "SEND_FINAL_POI";
    public final static String EXTRA_MESSAGE_SUB = "SEND_SUBSECTIONS";
    public final static String EXTRA_MESSAGE_POI = "SEND_POI";

    private ListView listView;
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
                } else {
                    intent.putExtra(EXTRA_MESSAGE_SUB, subSection.getPOIs()); //Todo: Change getPOIs to new method containing subsections
                }
                startActivity(intent);
            }
        });
    }

    private void openPOIs(ArrayList<PointOfInterest> poi) {
        System.out.println("Gets POIS");
        PointOfInterestAdapter adapter = new PointOfInterestAdapter(this, poi); //Creates adapter for sorting list into Points of Interest
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                PointOfInterest poi = (PointOfInterest) parent.getItemAtPosition(position);
                Intent intent = new Intent(TourActivity.this, PointOfInterestActivity.class);
                intent.putExtra(EXTRA_MESSAGE_FINAL, poi);
                startActivity(intent);
            }
        });
    }

}
