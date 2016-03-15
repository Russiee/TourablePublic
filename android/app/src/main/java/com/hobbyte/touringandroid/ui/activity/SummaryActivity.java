package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

public class SummaryActivity extends AppCompatActivity {

    public static final String KEY_ID = "keyID";
    public static final String TOUR_ID = "tourID";
    private static final String TAG = "SummaryActivity";
    private Button openButton;

    private LinearLayout updateTour;
    private ImageButton updateButton;
    private TextView updateText;

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

        (findViewById(R.id.updateTour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag().equals("ToUpdate")) {
                    doTourUpdate();
                } else if(v.getTag().equals("Updated")) {
                    //Do nothing
                }
            }
        });


    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(getApplicationContext()).close();

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

        updateButton = (ImageButton) findViewById(R.id.updateTourButton);
        updateText = (TextView) findViewById(R.id.updateTourText);
        updateTour = (LinearLayout) findViewById(R.id.updateTour);

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
        } else {
            updateButton.setImageResource(R.mipmap.ic_check_black_24dp);
            updateButton.setColorFilter(Color.parseColor("#00ff0f"));
            updateTour.setTag("Updated");
            updateText.setVisibility(View.GONE);
        }
    }

    private void displayUpdateOption() {

        TextView version = (TextView) findViewById(R.id.txtVersion);
        version.setText(getApplicationContext().getString(
                R.string.summary_activity_new_version_is_available));

        updateText.setVisibility(View.VISIBLE);
        updateText.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        updateButton.setImageResource(R.mipmap.ic_get_app_black_24dp);
        updateButton.setColorFilter(getResources().getColor(R.color.colorPrimaryLight));
        updateTour.setTag("ToUpdate");


    }

    private void displayExpiry() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        String expiryText = prefs.getString(App.context.getString(R.string.prefs_current_expiry), null);
        TextView txtExpiry = (TextView) findViewById(R.id.txtExpiry);
        txtExpiry.setText("Expires on: " + expiryText);
        //TODO implement this
    }



    public void doTourUpdate() {
        //TODO replace this when we have a download dialog
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
        this.finish();
    }

}
