package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
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

        displayTourInfo();
        displayVersionAndUpdate();
        displayExpiry();

        (findViewById(R.id.buttonStartTour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTourActivity();
            }
        });

        (findViewById(R.id.updateTourButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTourUpdate();
            }
        });


    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(this).close();

        super.onPause();
    }


    public void openTourActivity() {
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.INTENT_KEY_ID, keyID);
        intent.putExtra(TourActivity.INTENT_TITLE, title);
        startActivity(intent);
        this.finish();
    }

    private void displayTourInfo() {

        JSONObject tourJSON = FileManager.getJSON(getApplicationContext(), keyID, FileManager.TOUR_JSON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView txtDescription = (TextView) findViewById(R.id.txtTourDescription);
        TextView timeTourTakes = (TextView) findViewById(R.id.txtEstimatedTime);

        try {

            toolbar.setTitle(tourJSON.getString("title"));
            txtDescription.setText(tourJSON.getString("description"));
            timeTourTakes.setText("Estimated time: No value from api");

        } catch (JSONException e) {
            e.printStackTrace();
            toolbar.setTitle("Error");
            txtDescription.setText("Error");
        }
    }

    private void displayVersionAndUpdate() {

        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                context.MODE_PRIVATE);

        Set<String> updateSet = prefs.getStringSet(context.getString(R.string.prefs_tours_to_update), null);

        if (updateSet != null) {
            for (String s : updateSet) {
                if (s.equals(keyID)) {
                    displayUpdateOption();
                    break;
                }
            }
        }
    }

    private void displayUpdateOption() {

        TextView version = (TextView) findViewById(R.id.txtVersion);
        version.setText(getApplicationContext().getString(
                R.string.summary_activity_new_version_is_available));

        Button updateButton = (Button) findViewById(R.id.updateTourButton);
        updateButton.setVisibility(View.VISIBLE);

    }

    private void displayExpiry() {

        JSONObject keyJSON = FileManager.getJSON(getApplicationContext(),
                keyID, FileManager.KEY_JSON);

            try {

                String expiryDateString = keyJSON.getString("expiresAt");

                TextView txtExpiry = (TextView) findViewById(R.id.txtExpiry);
                txtExpiry.setText("Expiry needs implementing");
                //TODO implement this

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }



    public void doTourUpdate() {
        //TODO open download dialog
    }

}
