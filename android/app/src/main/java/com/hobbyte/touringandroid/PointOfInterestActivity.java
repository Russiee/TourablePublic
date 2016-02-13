package com.hobbyte.touringandroid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PointOfInterestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_interest);

        loadInformation();
    }

    private void loadInformation() {

        Intent intent = getIntent();
        PointOfInterest poi = (PointOfInterest) intent.getSerializableExtra(TourActivity.EXTRA_MESSAGE_FINAL);
        System.out.println(poi.toString());
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.poiLayout);
        View poiText = getLayoutInflater().inflate(R.layout.points_of_interest, layout, false);
        layout.addView(poiText);
        TextView text = (TextView) findViewById(R.id.pointsOfInterestText);
        text.setText(poi.toString());
    }
}