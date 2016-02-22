package com.hobbyte.touringandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hobbyte.touringandroid.helpers.FileManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SummaryActivity extends Activity {

    private static final String TAG = "SummaryActivity";

    public static final String KEY_ID = "keyID";

    private String keyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);

        loadTourDescription();
    }

    public void openTourActivity(View v) {
        // start tour
    }

    private void loadTourDescription() {
        JSONObject tourJSON = FileManager.getTourJSON(keyID);

        TextView txtTitle = (TextView) findViewById(R.id.txtTourTitle);
        TextView txtDescription = (TextView) findViewById(R.id.txtTourDescription);

        try {
            txtTitle.setText(tourJSON.getString("title"));
            txtDescription.setText(tourJSON.getString("description"));
        } catch (Exception e) {
            e.printStackTrace();
            txtTitle.setText("Error");
            txtDescription.setText("Error");
        }
    }
}
