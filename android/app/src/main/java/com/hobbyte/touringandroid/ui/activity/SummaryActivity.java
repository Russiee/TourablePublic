package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;

import org.json.JSONObject;

import java.util.Set;

public class SummaryActivity extends AppCompatActivity {

    private static final String TAG = "SummaryActivity";
    public static final String KEY_ID = "keyID";

    private Button openButton;
    private Button updateButton;

    private String keyID;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        Set<String> toUpdate = prefs.getStringSet(getString(R.string.prefs_tours_to_update), null);

//        if (true) {
        if (toUpdate != null && toUpdate.contains(keyID)) {
            updateButton = (Button) findViewById(R.id.buttonUpdateTour);
            updateButton.setVisibility(View.VISIBLE);
        }

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

    public void doTourUpdate(View v) {
        openButton.setEnabled(false);
        updateButton.setEnabled(false);

        new UpdateTask().execute();
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
    }

    private class UpdateTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            return null;
        }
    }
}
