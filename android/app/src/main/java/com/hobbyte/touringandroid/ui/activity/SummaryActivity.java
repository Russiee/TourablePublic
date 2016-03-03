package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;

import org.json.JSONObject;

public class SummaryActivity extends AppCompatActivity {

    private static final String TAG = "SummaryActivity";
    public static final String KEY_ID = "keyID";

    private String keyID;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);

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


    @Override
    public void onBackPressed() {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
    }
}
