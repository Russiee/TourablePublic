package com.hobbyte.touringandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.hobbyte.touringandroid.helpers.FileManager;
import com.hobbyte.touringandroid.internet.ServerAPI;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";

    private EditText textKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        boolean isFreshInstall = prefs.getBoolean(getString(R.string.prefs_is_new_install), true);

        if (isFreshInstall) { FileManager.makeTourDir(this); }

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
        textKey = (EditText) findViewById(R.id.textEnterTour);
        String tourKey = textKey.getText().toString();

        KeyCheckTask k = new KeyCheckTask();
        k.execute(tourKey);
    }

    /**
     * Called from KeyCheckTask when a valid key is entered. It takes the user to an Activity with
     * download options for the tour media.
     */
    private void goToTourDownload() {
        // TODO: this currently goes to a new tour activity, but needs to go to the download screen

        String tourKey = textKey.getText().toString();
        textKey.setText("");

        // move to next activity
        Tour testTour = new Tour();
        ArrayList<SubSection> subsectionList = new ArrayList<SubSection>();
        subsectionList = testTour.getSubSections();

        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.EXTRA_MESSAGE_SUB, subsectionList);
        startActivity(intent);
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

    /**
     * Asynchronously checks the server to see if a key which the user entered is a real, valid key.
     * If the key is valid, take the user to the next activity.
     */
    private class KeyCheckTask extends AsyncTask<String, Void, Boolean> {
        private static final String TAG = "KeyCheckTask";

        @Override
        protected Boolean doInBackground(String... params) {
            String key = params[0];

            boolean isValid = ServerAPI.checkKeyValidity(key);

            // TODO: change this when we have some real keys on the server
            return (isValid || key.equals("jeroenTour"));
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            if (isValid) {
                goToTourDownload();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid tour key", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();
                textKey.setText("");
            }
        }
    }
}
