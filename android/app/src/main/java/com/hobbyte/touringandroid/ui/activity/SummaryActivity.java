package com.hobbyte.touringandroid.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.Tour;
import com.hobbyte.touringandroid.tourdata.TourBuilder;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class SummaryActivity extends Activity {

    private static final String TAG = "SummaryActivity";
    public static final String KEY_ID = "keyID";

    private String keyID;
    private String title;

    private Pattern p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);
        p = Pattern.compile("https?:\\/\\/[\\w\\.\\/]*\\/(\\w*\\.(jpe?g|png))");

        /*TourBuilder tb = new TourBuilder(keyID, this);
        tb.start();*/

        loadTourDescription();
    }

    public void openTourActivity(View v) {
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.INTENT_KEY_ID, keyID);
        intent.putExtra(TourActivity.INTENT_TITLE, title);
        startActivity(intent);
    }

    private void loadTourDescription() {
        JSONObject tourJSON = FileManager.getJSON(getApplicationContext(), keyID, FileManager.TOUR_JSON);

        TextView txtTitle = (TextView) findViewById(R.id.txtTourTitle);
        TextView txtDescription = (TextView) findViewById(R.id.txtTourDescription);

        try {
            title = tourJSON.getString("title");
            txtTitle.setText(title);
            txtDescription.setText(tourJSON.getString("description"));
        } catch (Exception e) {
            e.printStackTrace();
            txtTitle.setText("Error");
            txtDescription.setText("Error");
        }
    }


}
