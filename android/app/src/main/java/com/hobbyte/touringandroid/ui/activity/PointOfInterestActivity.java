package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;

import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.ui.adapter.PoiContentAdapter;
import com.hobbyte.touringandroid.tourdata.PointOfInterest;
import com.hobbyte.touringandroid.R;

import java.util.ArrayList;

/**
 * Activity containing final points of interest
 */
public class PointOfInterestActivity extends Activity {

    private ListView listView;
    private static String keyID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_interest);

        loadInformation();
    }

    /**
     * Load information contained within the Points of Interest and allocate them
     */
    private void loadInformation() {

        /*listView = (ListView) findViewById(R.id.poiListView);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(SummaryActivity.KEY_ID);
        System.out.println(keyID);
        PointOfInterest poi = (PointOfInterest) intent.getSerializableExtra(TourActivity.EXTRA_MESSAGE_FINAL);

        ArrayList<String> content = poi.getContent();

        final ListViewItem[] items = new ListViewItem[content.size()];
        String url = "";

        //Separate data from point of interest class into text and a URL Website
        for (int i = 0; i < content.size(); i++) {
            String info = content.get(i);
            if (info.contains(".jpg")) {
                items[i] = new ListViewItem(info, PoiContentAdapter.IMG);
                url = info;
            } else {
                items[i] = new ListViewItem(info, PoiContentAdapter.TEXT);
            }
        }

        PoiContentAdapter adapter = new PoiContentAdapter(this, items, keyID);
        listView.setAdapter(adapter);*/
    }
}
