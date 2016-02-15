package com.hobbyte.touringandroid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Activity containing final points of interest
 */
public class PointOfInterestActivity extends Activity {

    private ListView listView;
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

        listView = (ListView) findViewById(R.id.poiListView);

        Intent intent = getIntent();
        PointOfInterest poi = (PointOfInterest) intent.getSerializableExtra(TourActivity.EXTRA_MESSAGE_FINAL);

        ArrayList<String> content = poi.getContent();

        final ListViewItem[] items = new ListViewItem[content.size()];
        String url = "";

        //Separate data from point of interest class into text and a URL Website
        for (int i = 0; i < content.size(); i++) {
            String info = content.get(i);
            if (info.contains("http")) {
                items[i] = new ListViewItem(info, PoiContentAdapter.IMG);
                url = info;
            } else {
                items[i] = new ListViewItem(info, PoiContentAdapter.TEXT);
            }
        }

        PoiContentAdapter adapter = new PoiContentAdapter(this, items);
        listView.setAdapter(adapter);
    }
}
