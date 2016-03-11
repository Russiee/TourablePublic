package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONObject;

import java.util.Set;

public class SummaryActivity extends AppCompatActivity {

    public static final String KEY_ID = "keyID";
    public static final String TOUR_ID = "tourID";
    private static final String TAG = "SummaryActivity";
    private Button openButton;
    private Button updateButton;

    private String keyID;
    private String tourID;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);
        tourID = intent.getStringExtra(TOUR_ID);

        Log.d(TAG, String.format("k: %s t: %s", keyID, tourID));

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        Set<String> toUpdate = prefs.getStringSet(getString(R.string.prefs_tours_to_update), null);

        loadTourDescription();
        displayTourInfo();

        if (true) {
//     if (toUpdate != null && toUpdate.contains(keyID)) {
            updateButton = (Button) findViewById(R.id.updateTourButton);
            updateButton.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txtVersion)).setText("Your version is old");
        }



        ((Button) findViewById(R.id.buttonStartTour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTourActivity();
            }
        });

    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(this).close();

        super.onPause();
    }

    /**
     * Sets the information on the summary screen
     */
    private void displayTourInfo() {
        TextView time = (TextView) findViewById(R.id.txtEstimatedTime);
        TextView version = (TextView) findViewById(R.id.txtVersion);
        TextView expiry = (TextView) findViewById(R.id.txtExpiry);

        time.setText("Estimated time: n/a");
        version.setText("Your version is current");
        expiry.setText("Your key expires in " + 23 + " days");

    }

    public void openTourActivity() {
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.INTENT_KEY_ID, keyID);
        intent.putExtra(TourActivity.INTENT_TITLE, title);
        startActivity(intent);
        this.finish();
    }

    private void loadTourDescription() {
        JSONObject tourJSON = FileManager.getJSON(getApplicationContext(), keyID, FileManager.TOUR_JSON);

        TextView txtTitle = (TextView) findViewById(R.id.txtTourTitle);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        TextView txtDescription = (TextView) findViewById(R.id.txtTourDescription);

        try {
            title = tourJSON.getString("title");
            toolbar.setTitle(title);
            txtDescription.setText(tourJSON.getString("description"));
        } catch (Exception e) {
            e.printStackTrace();
            txtTitle.setText("Error");
            txtDescription.setText("Error");
        }
    }

    public void doTourUpdate(View v) {
        //TODO open download dialog
    }

}
