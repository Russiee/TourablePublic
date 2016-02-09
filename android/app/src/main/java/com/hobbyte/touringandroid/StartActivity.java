package com.hobbyte.touringandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class StartActivity extends Activity {
    public static final String TAG = "StartActivity";
    public static final String EXTRA_MESSAGE = "com.hobbyte.touringandroid.StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        loadPreviousTours();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // needed in the event that a user adds a tour and then returns to this screen
        loadPreviousTours();
    }

    /**
     * Checks if the provided tour key is valid. If so, continue to the next activity, otherwise
     * inform the user that the key was invalid.
     * @param v the submit button
     */
    public void checkTourKey(View v) {
        // TODO: hook this up to the api to securely validate the key

        EditText textKey = (EditText) findViewById(R.id.textEnterTour);
        String tourKey = textKey.getText().toString();

        // this will be changed in the future
        if (tourKey.equals("jeroenTour")) {
            Log.d(TAG, "Valid key");
            // move to next activity
            Tour testTour = new Tour();
            ArrayList<SubSection> subsectionList = new ArrayList<SubSection>();
            subsectionList = testTour.getSubSections();
            Intent intent = new Intent(this, TourActivity.class);
            intent.putExtra(this.EXTRA_MESSAGE, subsectionList);
            startActivity(intent);
        } else {
            Log.d(TAG, "Invalid key");
            Toast toast = Toast.makeText(this, "Invalid tour key", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 20);
            toast.show();
            textKey.setText("");
        }
    }

    /**
     * If the user has tours saved to the device, show their names and expiry information.
     */
    private void loadPreviousTours() {
        // TODO: need to finalise design
        // TODO: hook up to filesystem to check if tours exist

        LinearLayout layout = (LinearLayout) findViewById(R.id.previousToursLayout);
        View noToursText = getLayoutInflater().inflate(R.layout.text_no_tours, layout, false);

        layout.addView(noToursText);
    }
}
